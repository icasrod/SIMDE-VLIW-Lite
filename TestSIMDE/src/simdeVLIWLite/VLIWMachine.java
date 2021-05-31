/**
 * 
 */
package simdeVLIWLite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.TreeMap;

import simdeVLIWLite.LongInstruction.LongInstructionJumpOperation;
import simdeVLIWLite.LongInstruction.LongInstructionOperation;

/**
 * @author Iv�n Castilla
 *
 */
public class VLIWMachine {
	private final static String STR_START_DIR = "[";
	private final static int NREG = 64;
	private final static int NMEM = 1024;
	private final GPRegisterBank gpr;
	private final FPRegisterBank fpr;
	private final Memory mem;

	private final PriorityQueue<Action> actionList;
	private boolean debugMode = false;
	
	/**
	 * 
	 */
	public VLIWMachine(TreeMap<FunctionalUnit, Integer> configuration) {
		gpr = new GPRegisterBank(NREG);
		fpr = new FPRegisterBank(NREG);
		mem = new Memory(NMEM);
		actionList = new PriorityQueue<>();
	}

	/**
	 * Carga la memoria y los registros desde un fichero que, al menos, debe tener 3 l�neas con las cadenas
	 * "#GPR", "#FPR" y "#MEM".
	 * @param fileName
	 */
	public void loadMemoryAndRegisters(String fileName) {
		final File memFile = new File(fileName);
		Scanner scan;
		int what = -1;
		try {
			scan = new Scanner(memFile);
			// Todos los ficheros est�n con el "." para separar los decimales
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
					// Direcci�n
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
	private int execute(LongInstructionOperation op, int pc) {
		int op1, op2;
		double opFP1, opFP2;
		final Instruction inst = op.getInstruction();
		switch (inst.getOpcode()) {
		case ADD:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], op1 + op2);
			pc++;
			break;
		case ADDF:
			opFP1 = fpr.read(inst.getOp()[1]);
			opFP2 = fpr.read(inst.getOp()[2]);
			fpr.write(inst.getOp()[0], opFP1 + opFP2);
			pc++;
			break;
		case ADDI:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = inst.getOp()[2];
			gpr.write(inst.getOp()[0], op1 + op2);
			pc++;
			break;
		case AND:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], op1 & op2);
			pc++;
			break;
		case BEQ:
			op1 = gpr.read(inst.getOp()[0]);
			op2 = gpr.read(inst.getOp()[1]);
			if (op1 == op2)
				pc = inst.getOp()[2];
			else
				pc++;
			break;
		case BGT:
			op1 = gpr.read(inst.getOp()[0]);
			op2 = gpr.read(inst.getOp()[1]);
			if (op1 > op2)
				pc = inst.getOp()[2];
			else
				pc++;
			break;
		case BNE:
			op1 = gpr.read(inst.getOp()[0]);
			op2 = gpr.read(inst.getOp()[1]);
			if (op1 != op2)
				pc = ((LongInstructionJumpOperation)op).getDestination();
			else
				pc++;
			break;
		case LF:
			op1 = inst.getOp()[1];
			op2 = gpr.read(inst.getOp()[2]);
			try {
				fpr.write(inst.getOp()[0], mem.read(op1 + op2));
			} catch (SIMDEException e) {
				e.printStackTrace();
			}
			pc++;
			break;
		case LW:
			op1 = inst.getOp()[1];
			op2 = gpr.read(inst.getOp()[2]);
			try {
				gpr.write(inst.getOp()[0], (int)mem.read(op1 + op2));
			} catch (SIMDEException e) {
				e.printStackTrace();
			}
			pc++;
			break;
		case MULT:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], op1 * op2);
			pc++;
			break;
		case MULTF:
			opFP1 = fpr.read(inst.getOp()[1]);
			opFP2 = fpr.read(inst.getOp()[2]);
			fpr.write(inst.getOp()[0], opFP1 * opFP2);
			pc++;
			break;
		case NOR:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], ~(op1 | op2));
			pc++;
			break;
		case OR:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], op1 | op2);
			pc++;
			break;
		case SF:
			op1 = inst.getOp()[1];
			op2 = gpr.read(inst.getOp()[2]);
			try {
				mem.write(op1 + op2, fpr.read(inst.getOp()[0]));
			} catch (SIMDEException e) {
				e.printStackTrace();
			}
			pc++;
			break;
		case SLLV:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], op1 << op2);
			pc++;
			break;
		case SRLV:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], op1 >> op2);
			pc++;
			break;
		case SUB:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], op1 - op2);
			pc++;
			break;
		case SUBF:
			opFP1 = fpr.read(inst.getOp()[1]);
			opFP2 = fpr.read(inst.getOp()[2]);
			fpr.write(inst.getOp()[0], opFP1 - opFP2);
			pc++;
			break;
		case SW:
			op1 = inst.getOp()[1];
			op2 = gpr.read(inst.getOp()[2]);
			try {
				mem.write(op1 + op2, gpr.read(inst.getOp()[0]));
			} catch (SIMDEException e) {
				e.printStackTrace();
			}
			pc++;
			break;
		case XOR:
			op1 = gpr.read(inst.getOp()[1]);
			op2 = gpr.read(inst.getOp()[2]);
			gpr.write(inst.getOp()[0], op1 ^ op2);
			pc++;
			break;
		default:
			pc++;
			break;
		
		}
		return pc;
	}
	
	public int execute(VLIWCode code) {
		int cycle = 0;
		int pc = 0;
		
		LongInstruction inst = code.getInstruction(pc); 
		while (inst != null) {
			if (debugMode)
				System.out.println("CYCLE: " + cycle + "\tPC: " + pc);
			final ArrayList<LongInstructionOperation> opers = inst.getValidOperations();
			// Planificamos las instrucciones que deben ejecutarse
			for (LongInstructionOperation op : opers) {
				if (debugMode)
					System.out.println("\tSTART_EXE:" + op.getInstruction().getId());
				actionList.add(new Action(cycle + op.getInstruction().getOpcode().getFU().getLatency() - 1, op));
			}
			// Ejecutamos todas las intrucciones planificadas para este ciclo
			boolean noMore = false;
			int newPC = pc + 1;
			while (!actionList.isEmpty() && !noMore) {
				if (actionList.peek().getCycle() != cycle)
					noMore = true;
				else {
					final Action action = actionList.poll();
					newPC = execute(action.getOper(),pc);
					if (debugMode)
						System.out.println("\tEND_EXE:" + action.getOper().getInstruction().getId());
				}
			}
			pc = newPC;
			cycle++;
			inst = code.getInstruction(pc); 
		}	
		// Se acaba con las intrucciones pendientes
		while (!actionList.isEmpty()) {
			Action action = actionList.poll();
			while (cycle < action.getCycle()) 
				System.out.println("CYCLE: " + (cycle++) + "\tPC: " + (pc++));				
			// Ejecutamos todas las intrucciones planificadas para este ciclo, incluida la primera que ya separamos
			int newPC = execute(action.getOper(), pc);
			if (debugMode)
				System.out.println("\tEND_EXE:" + action.getOper().getInstruction().getId());
			boolean noMore = false;			
			while (!actionList.isEmpty() && !noMore) {
				if (actionList.peek().getCycle() != cycle)
					noMore = true;
				else {
					action = actionList.poll();
					newPC = execute(action.getOper(),pc);
					if (debugMode)
						System.out.println("\tEND_EXE:" + action.getOper().getInstruction().getId());
				}
			}
			pc = newPC;
			cycle++;
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
		/**
		 * @param cycle
		 * @param oper
		 */
		public Action(int cycle, LongInstructionOperation oper) {
			this.cycle = cycle;
			this.oper = oper;
		}
		
		public int getCycle() {
			return cycle;
		}
		
		public LongInstructionOperation getOper() {
			return oper;
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
