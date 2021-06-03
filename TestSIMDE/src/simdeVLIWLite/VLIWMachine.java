/**
 * 
 */
package simdeVLIWLite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import simdeVLIWLite.LongInstruction.LongInstructionJumpOperation;
import simdeVLIWLite.LongInstruction.LongInstructionOperation;

/**
 * @author Iván Castilla
 *
 */
public class VLIWMachine {
	private final static String STR_START_DIR = "[";
	private final static int NREG = 64;
	private final static int NMEM = 1024;
	private final GPRegisterBank gpr;
	private final FPRegisterBank fpr;
	private final Memory mem;
	private final PredicateRegisterBank pred;

	private final PriorityQueue<Action> actionList;
	private boolean debugMode = false;
	private final Random rnd;
	
	/**
	 * 
	 */
	public VLIWMachine(TreeMap<FunctionalUnit, Integer> configuration, int cacheFailRate, int cacheFailPenalty) {
		gpr = new GPRegisterBank(NREG);
		fpr = new FPRegisterBank(NREG);
		mem = new Memory(NMEM, (double)cacheFailRate / 100.0, cacheFailPenalty);
		pred = new PredicateRegisterBank(NREG);
		actionList = new PriorityQueue<>();
		this.rnd = new Random();
	}

	/**
	 * Carga la memoria y los registros desde un fichero que, al menos, debe tener 3 líneas con las cadenas
	 * "#GPR", "#FPR" y "#MEM".
	 * @param fileName
	 */
	public void loadMemoryAndRegisters(String fileName) {
		final File memFile = new File(fileName);
		Scanner scan;
		int what = -1;
		try {
			scan = new Scanner(memFile);
			// Todos los ficheros están con el "." para separar los decimales
			scan.useLocale(Locale.ENGLISH);
			while (scan.hasNext()) {
				final String text = scan.next();
				if (GPRegisterBank.STR.equals(text)) {
					what = 0;					
				}
				else if (FPRegisterBank.STR.equals(text)) {
					what = 1;					
				}
				else if (Memory.STR.equals(text)) {
					what = 2;					
				}
				else if (text.startsWith(STR_START_DIR)) {
					// Dirección
					int dir = Integer.parseInt(text.substring(1, text.length() - 1));
					switch(what) {
					case 0:
						while (scan.hasNextInt()) {
							gpr.write(dir++, scan.nextInt());
						}
						break;
					case 1:
						while (scan.hasNextDouble()) {
							fpr.write(dir++, scan.nextDouble());
						}
						break;
					case 2:
						while (scan.hasNextDouble()) {
							mem.write(dir++, scan.nextDouble());
						}
						break;
					default:
						break;
					}
				}
			}
			scan.close();
		} catch (FileNotFoundException | SIMDEException e) {
			e.printStackTrace();
		}
	}

	public void printMemoryAndRegisters() {
		System.out.print(gpr);
		System.out.print(fpr);
		System.out.print(mem);
		
	}
	
	public void reset() {
		gpr.reset();
		fpr.reset();
		mem.reset();
		pred.reset();
	}
	
	private void setOperandValues(LongInstructionOperation op) {
		double []values = new double[2];
		final Instruction inst = op.getInstruction();
		switch (inst.getOpcode()) {
		case ADD:
		case SUB:
		case AND:
		case NOR:
		case OR:
		case XOR:
		case MULT:
		case SLLV:
		case SRLV:
			values[0] = gpr.read(inst.getOp()[1]);
			values[1] = gpr.read(inst.getOp()[2]);
			break;
		case ADDF:
		case SUBF:
		case MULTF:
			values[0] = fpr.read(inst.getOp()[1]);
			values[1] = fpr.read(inst.getOp()[2]);
			break;
		case ADDI:
			values[0] = gpr.read(inst.getOp()[1]);
			values[1] = inst.getOp()[2];
			break;
		case BEQ:
		case BGT:
		case BNE:
			values[0] = gpr.read(inst.getOp()[0]);
			values[1] = gpr.read(inst.getOp()[1]);
			break;
		case LF:
		case LW:
			values[0] = inst.getOp()[1] + gpr.read(inst.getOp()[2]);
			values[1] = 0.0;
			break;
		case SF:
			values[0] = inst.getOp()[1] + gpr.read(inst.getOp()[2]);
			values[1] = fpr.read(inst.getOp()[0]);
			break;
		case SW:
			values[0] = inst.getOp()[1] + gpr.read(inst.getOp()[2]);
			values[1] = gpr.read(inst.getOp()[0]);
			break;
		default:
			break;
		
		}
		op.setOperandValues(values);
	}
	
	private int execute(Action action, int pc) throws SIMDEException {
		final LongInstructionOperation op = action.getOper();
		if (pred.read(op.getPred())) {
			int op1, op2;
			double opFP1, opFP2;
			final Instruction inst = op.getInstruction();
			switch (inst.getOpcode().getFU()) {
			case FP_ADD:
				opFP1 = op.getOperand1Value();
				opFP2 = op.getOperand2Value();
				switch(inst.getOpcode()) {
				case ADDF:	fpr.write(inst.getOp()[0], opFP1 + opFP2);	break;
				case SUBF:	fpr.write(inst.getOp()[0], opFP1 - opFP2);	break;
				default:
					throw new SIMDEException("Código de operación inesperado en la unidad de suma de punto flotante: " + inst.getOpcode());
				}
				pc++;
				break;
			case FP_MULT:
				opFP1 = op.getOperand1Value();
				opFP2 = op.getOperand2Value();
				if (Opcode.MULTF.equals(inst.getOpcode()))
					fpr.write(inst.getOp()[0], opFP1 * opFP2);
				else
					throw new SIMDEException("Código de operación inesperado en la unidad de multiplicación de punto flotante: " + inst.getOpcode());
				pc++;
				break;
			case INT_ADD:
				op1 = (int)op.getOperand1Value();
				op2 = (int)op.getOperand2Value();
				switch (inst.getOpcode()) {
				case ADD:
				case ADDI:	gpr.write(inst.getOp()[0], op1 + op2);		break;
				case AND:	gpr.write(inst.getOp()[0], op1 & op2);		break;
				case NOR:	gpr.write(inst.getOp()[0], ~(op1 | op2));	break;
				case OR:	gpr.write(inst.getOp()[0], op1 | op2);		break;
				case SLLV:	gpr.write(inst.getOp()[0], op1 << op2);		break;
				case SRLV:	gpr.write(inst.getOp()[0], op1 >> op2);		break;
				case SUB:	gpr.write(inst.getOp()[0], op1 - op2);		break;
				case XOR:	gpr.write(inst.getOp()[0], op1 ^ op2);		break;
				default:
					throw new SIMDEException("Código de operación inesperado en la unidad de suma entera: " + inst.getOpcode());
				}
				pc++;
				break;
			case INT_MULT:
				op1 = (int)op.getOperand1Value();
				op2 = (int)op.getOperand2Value();
				if (Opcode.MULT.equals(inst.getOpcode()))
					gpr.write(inst.getOp()[0], op1 * op2);
				else
					throw new SIMDEException("Código de operación inesperado en la unidad de multiplicación entera: " + inst.getOpcode());
				pc++;
				break;
			case JUMP:
				op1 = (int)op.getOperand1Value();
				op2 = (int)op.getOperand2Value();
				boolean cond = false;
				switch (inst.getOpcode()) {
				case BEQ: cond = (op1 == op2);	break;
				case BGT: cond = (op1 > op2);	break;
				case BNE: cond = (op1 != op2);	break;
				default:
					throw new SIMDEException("Código de operación inesperado en la unidad de salto: " + inst.getOpcode());
				}
				pc = cond ? ((LongInstructionJumpOperation)op).getDestination() : pc + 1;
				pred.write(((LongInstructionJumpOperation)op).getPredTrue(), cond);
				pred.write(((LongInstructionJumpOperation)op).getPredFalse(), !cond);
				break;
			case MEM:
				if (action.isCached()) {
					switch (inst.getOpcode()) {
					case LF: fpr.write(inst.getOp()[0], mem.read((int)op.getOperand1Value()));		break;
					case LW: gpr.write(inst.getOp()[0], (int)mem.read((int)op.getOperand1Value()));	break;
					case SF: mem.write((int)op.getOperand1Value(), op.getOperand2Value());			break;
					case SW: mem.write((int)op.getOperand1Value(), (int)op.getOperand2Value());		break;
					default:
						throw new SIMDEException("Código de operación inesperado en la unidad de memoria: " + inst.getOpcode());
					}
					pc++;
				}
				else {
					throw new CacheFailException();
				}
				break;
			default:
				throw new SIMDEException("Unidad funcional desconocida: " + inst.getOpcode().getFU());
			}
			if (debugMode)
				System.out.println("\tFINAL:\t" + op.getInstruction());
		}
		else  {
			if (debugMode) {
				System.out.println("\tCANCELADA:\t" + op.getInstruction());						
			}
			pc = pc + 1;
		}
		return pc;
	}

	/**
	 * Planifica las operaciones para que se terminen de ejecutar cuando corresponda
	 * TODO: De momento, no tiene en cuenta fallos de caché
	 * @param inst Instrucción larga que contiene las operaciones a planificar
	 * @param cycle Ciclo de reloj en que se está comenzando la ejecución de esta instrucción larga
	 */
	private void schedule(LongInstruction inst, int cycle) {
		final ArrayList<LongInstructionOperation> opers = inst.getValidOperations();
		for (LongInstructionOperation op : opers) {
			if (debugMode)
				System.out.println("\tCOMIENZO:\t" + op.getInstruction());
			setOperandValues(op);
			actionList.add(new Action(cycle + op.getInstruction().getOpcode().getFU().getLatency() - 1, op));
		}
	}
	
	private ArrayList<Action> getValidActions(int cycle) {
		final ArrayList<Action> list = new ArrayList<>();
		boolean more = true;
		while (more) {
			if (actionList.isEmpty())
				more = false;
			else {
				if (actionList.peek().getCycle() == cycle)
					list.add(actionList.poll());
				else
					more = false;
			}
		}
		return list;
	}
	
	/**
	 * Añade una burbuja en la ejecución por, por ejemplo, un fallo de caché.
	 * 
	 * Lo que hace es replanificar todas las tareas.
	 * @param latency Penalización en ciclos de ejecución
	 */
	private void addStall(int latency) {
		Action[] actions = new Action[actionList.size()];
		actions = actionList.toArray(actions);
		actionList.clear();
		for (Action prevAction : actions) {
			actionList.add(new Action(prevAction, latency));
		}
	}
	
	public int execute(VLIWCode code) {
		int cycle = 0;
		int pc = 0;
		boolean stop = false;
		
		if (debugMode)
			System.out.println("CICLO: " + cycle + "\tPC: " + pc);
		try {
			schedule(code.getInstruction(pc), cycle);
			final ArrayList<Action> pendingActions = new ArrayList<>();
			do {
				int newPC = pc + 1;
				// Se ejecutan las operaciones correspondientes a este ciclo
				final ArrayList<Action> actions = getValidActions(cycle);
				for (Action action : actions) {
					try {
						newPC = execute(action, pc);
					} catch (CacheFailException e) {
						// Colocamos la acción que provocó el fallo en la lista de pendientes
						pendingActions.add(action);
					}
				}
				// Si hubo fallos de caché, tenemos que meter burbujas en la ejecución. 
				// Además, retrasamos la ejecución de las tareas pendientes. La forma no es muy eficiente (sacar todas las acciones y volver a meterlas, pero es lo más seguro
				// También esto asegura que la acción que provocó el fallo de caché se replanifique cuando corresponda
				if (pendingActions.size() > 0) {
					addStall(mem.getCacheMissPenalty());
					cycle += mem.getCacheMissPenalty();
					if (debugMode) {
						System.out.println("¡FALLO CACHE! Añadiendo penalización: " + mem.getCacheMissPenalty());
						System.out.println("CICLO: " + cycle + "\tPC: " + pc);
					}
					// Ejecutamos las acciones que provocaron el fallo (puede haber más de un fallo de caché)
					while (pendingActions.size() > 0) {
						final Action action = pendingActions.remove(0);
						// No puede fallar dos veces seguidas
						action.setCached();
						// Se lanza forzando ignorando el PC de salida, que se tuvo que haber calculado antes
						execute(action, pc);
					}
				}
				// Avanzamos el PC y el ciclo
				cycle++;
				pc = newPC;
				if (debugMode)
					System.out.println("CICLO: " + cycle + "\tPC: " + pc);
				// Planificamos las operaciones asociadas a la nueva instrucción larga
				if (code.isHalt(pc))
					stop = true;
				else {
					schedule(code.getInstruction(pc), cycle);
					stop = false;
				}
			} while (!stop || !actionList.isEmpty());
		} catch (SIMDEException e) {
			e.printStackTrace();
		}
		return cycle;
	}
	
	/**
	 * @return the debugMode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * @param debugMode the debugMode to set
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	private class Action implements Comparable<Action> {
		private final int cycle;
		private final LongInstructionOperation oper;
		private boolean cached = true;
		
		/**
		 * Crea una nueva acción para ejecutar la operación "oper" el ciclo "cycle"
		 * @param cycle
		 * @param oper
		 */
		public Action(int cycle, LongInstructionOperation oper) {
			this.cycle = cycle;
			this.oper = oper;
			if (FunctionalUnit.MEM.equals(oper.getInstruction().getOpcode().getFU())) {
				cached = (rnd.nextDouble() >= mem.getCacheMissRate());
			}
		}
		
		/**
		 * Crea una nueva acción copia de la anterior pero retrasada "latency" ciclos de ejecución
		 * @param prevAction Acción anteriormente planificada
		 * @param latency Penalización de latencia
		 */
		public Action(Action prevAction, int latency) {
			this.cycle = prevAction.cycle + latency;
			this.oper = prevAction.oper;
		}
		
		public int getCycle() {
			return cycle;
		}
		
		public LongInstructionOperation getOper() {
			return oper;
		}
		
		/**
		 * @return the cached
		 */
		public boolean isCached() {
			return cached;
		}

		/**
		 * @param cached the cached to set
		 */
		public void setCached() {
			this.cached = true;
		}

		@Override
		public int compareTo(Action o) {
			if (cycle < o.cycle)
				return -1;
			if (cycle > o.cycle)
				return 1;
			final FunctionalUnit fu = oper.getInstruction().getOpcode().getFU();
			final FunctionalUnit oFu = o.oper.getInstruction().getOpcode().getFU();
			if (fu.ordinal() < oFu.ordinal())
				return -1;
			if (fu.ordinal() > oFu.ordinal())
				return 1;
			return 0;
		}
	}
}
