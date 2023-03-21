/**
 * 
 */
package simdeVLIWLite;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

/**
 * La máquina VLIW simulada
 * @author Iván Castilla
 *
 */
public class SequentialMachine {
	/** Cadena de texto que identifica el comienzo de una dirección en un fichero de contenido de memoria y registros */
	private final static String STR_START_DIR = "[";
	/** Número de registros de cualquier tipo en la máquina */
	private final static int NREG = 64;
	/** Núimero de palabras de memoria en la máquina */
	private final static int NMEM = 1024;
	/** Banco de registros de propósito general */
	private final GPRegisterBank gpr;
	/** Banco de registros de punto flotante */
	private final FPRegisterBank fpr;
	/** Memoria */
	private final Memory mem;

	/** Modo de depuración */
	private boolean debugMode = false;
	/** Generador de números aleatorios para los fallos de caché */
	private final Random rnd;
	
	private final int[] latencies;
	private int pc;
	private int cycle;

	/**
	 * Crea una máquina VLIW
	 * @param latencies Latencia para cada tipo de UF incluida en la máquina
	 * @param cacheMissRate Tasa de fallos de la caché, expresada como un valor entre 0 y 100 (%).
	 * @param cacheMissPenalty Penalización en ciclos si se produce un fallo de la caché
	 */
	public SequentialMachine(int[] latencies, int cacheFailRate, int cacheFailPenalty) {
		gpr = new GPRegisterBank(NREG);
		fpr = new FPRegisterBank(NREG);
		mem = new Memory(NMEM, (double)cacheFailRate / 100.0, cacheFailPenalty);
		this.rnd = new Random();
		this.latencies = latencies;
	}

	/**
	 * Carga la memoria y los registros desde un fichero que, al menos, debe tener 3 líneas con las cadenas
	 * "#GPR", "#FPR" y "#MEM".
	 * @param fileName Nombre del fichero que define los contenidos de registros y memoria
	 */
	public void loadMemoryAndRegisters(String fileName) throws FileNotFoundException, SIMDEException {
		final File memFile = new File(fileName);
		Scanner scan;
		int what = -1;
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
	}

	/**
	 * Imprime por pantalla el contenido de memoria y registros
	 */
	public void printMemoryAndRegisters() {
		System.out.print(gpr);
		System.out.print(fpr);
		System.out.print(mem);
	}

	/**
	 * Resetea la máquina
	 */
	public void reset() {
		gpr.reset();
		fpr.reset();
		mem.reset();
	}
	
	/**
	 * Busca y establece los valores de los operandos en tiempo de ejecución
	 * @param op Operación que encapsula la instrucción secuencial que se quiere ejecutar
	 */
	private void setOperandValues(Action op) {
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
	
	/**
	 * Finaliza la ejecución de una instrucción
	 * @param action Acción que encapsula a la instrucción
	 * @param pc Contador de programa que indica cuándo se está ejecutando la instrucción
	 * @return El contador de programa resultante de la ejecución de esta instrucción
	 * @throws SIMDEException Errores de ejecución
	 */
	private void execute(Action action) throws SIMDEException {
		int op1, op2;
		double opFP1, opFP2;
		final Instruction inst = action.getInstruction();
		setOperandValues(action);
		switch (inst.getOpcode().getFU()) {
		case FP_ADD:
			opFP1 = action.getOperand1Value();
			opFP2 = action.getOperand2Value();
			switch(inst.getOpcode()) {
			case ADDF:	fpr.write(inst.getOp()[0], opFP1 + opFP2);	break;
			case SUBF:	fpr.write(inst.getOp()[0], opFP1 - opFP2);	break;
			default:
				throw new SIMDEException("Código de operación inesperado en la unidad de suma de punto flotante: " + inst.getOpcode());
			}
			pc++;
			break;
		case FP_MULT:
			opFP1 = action.getOperand1Value();
			opFP2 = action.getOperand2Value();
			if (Opcode.MULTF.equals(inst.getOpcode()))
				fpr.write(inst.getOp()[0], opFP1 * opFP2);
			else
				throw new SIMDEException("Código de operación inesperado en la unidad de multiplicación de punto flotante: " + inst.getOpcode());
			pc++;
			break;
		case INT_ADD:
			op1 = (int)action.getOperand1Value();
			op2 = (int)action.getOperand2Value();
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
			op1 = (int)action.getOperand1Value();
			op2 = (int)action.getOperand2Value();
			if (Opcode.MULT.equals(inst.getOpcode()))
				gpr.write(inst.getOp()[0], op1 * op2);
			else
				throw new SIMDEException("Código de operación inesperado en la unidad de multiplicación entera: " + inst.getOpcode());
			pc++;
			break;
		case JUMP:
			op1 = (int)action.getOperand1Value();
			op2 = (int)action.getOperand2Value();
			boolean cond = false;
			switch (inst.getOpcode()) {
			case BEQ: cond = (op1 == op2);	break;
			case BGT: cond = (op1 > op2);	break;
			case BNE: cond = (op1 != op2);	break;
			default:
				throw new SIMDEException("Código de operación inesperado en la unidad de salto: " + inst.getOpcode());
			}
			pc = cond ? inst.getOp()[2] : pc + 1;
			break;
		case MEM:
			switch (inst.getOpcode()) {
			case LF: fpr.write(inst.getOp()[0], mem.read((int)action.getOperand1Value()));		break;
			case LW: gpr.write(inst.getOp()[0], (int)mem.read((int)action.getOperand1Value()));	break;
			case SF: mem.write((int)action.getOperand1Value(), action.getOperand2Value());			break;
			case SW: mem.write((int)action.getOperand1Value(), (int)action.getOperand2Value());		break;
			default:
				throw new SIMDEException("Código de operación inesperado en la unidad de memoria: " + inst.getOpcode());
			}
			pc++;
			if (!action.isCached()) {
				System.out.println("¡FALLO CACHE! Añadiendo penalización: " + mem.getCacheMissPenalty());
				cycle += mem.getCacheMissPenalty();
			}
			break;
		default:
			throw new SIMDEException("Unidad funcional desconocida: " + inst.getOpcode().getFU());
		}
		cycle += latencies[inst.getOpcode().getFU().ordinal()];
		if (debugMode)
			System.out.println("\tFINAL:\t" + inst);
	}

	/**
	 * Ejecuta el código de instrucciones largas indicado en esta máquina
	 * @param code Código de instrucciones largas
	 * @return El número de ciclos que tardó la ejecución
	 */
	public int execute(Code code) {
		final ArrayList<Instruction> instructions = code.getInstructions();
		
		if (debugMode)
			System.out.println("CICLO: " + cycle + "\tPC: " + pc);
		try {
			while (pc < code.getNLines()) { 
				final Action action = new Action(cycle, instructions.get(pc));
				execute(action);
				if (debugMode) {
					System.out.println("CICLO: " + cycle + "\tPC: " + pc);
				}
			}
		} catch (SIMDEException e) {
			e.printStackTrace();
		}
		return cycle;
	}
	
	/**
	 * Devuelve verdadero si la máquina está en modo de depuración
	 * @return Verdadero si la máquina está en modo de depuración
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Establece el modo de depuración de la máquina
	 * @param Verdadero si la máquina debe estar en modo de depuración; falso en otro caso.
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * Una estructura para poder simular la ejecución de las instrucciones. Indica una operación y el ciclo en que se espera que deba terminar la ejecución de la instrucción 
	 * @author Iván Castilla
	 *
	 */
	private class Action {
		/** Instrucción a finalizar */
		private final Instruction oper;
		/** En el caso de las instrucciones de acceso a memoria, indica si el acceso producirá un fallo de caché (valor false). En el resto de casos, siempre es verdadero */
		private final boolean cached;
		/** Valores de los operandos fuente durante la ejecución */
		private final double[]operandValues;
		
		/**
		 * Crea una nueva acción para ejecutar la operación "oper" el ciclo "cycle"
		 * @param cycle Ciclo de reloj en el que se va a finalizar la ejecución de esta instrucción
		 * @param oper Operación que encapsula la instrucción a finalizar
		 */
		public Action(int cycle, Instruction oper) {
			this.oper = oper;
			this.operandValues = new double[2];
			if (FunctionalUnit.MEM.equals(oper.getOpcode().getFU())) {
				cached = (rnd.nextDouble() >= mem.getCacheMissRate());
			}
			else {
				cached = true;
			}
		}
		
		/**
		 * Devuelve la instrucción a finalizar
		 * @return La instrucción a finalizar
		 */
		public Instruction getInstruction() {
			return oper;
		}
		
		/**
		 * Devuelve verdadero si es una instrucción de memoria y no produce fallo de caché, o si es cualquier otro tipo de instrucción.
		 * @return Verdadero si es una instrucción de memoria y no produce fallo de caché, o si es cualquier otro tipo de instrucción
		 */
		public boolean isCached() {
			return cached;
		}

		/**
		 * Devuelve el valor en tiempo de ejecución del primer operando fuente de la instrucción
		 * @return Valor en tiempo de ejecución del primer operando fuente de la instrucción
		 */
		public double getOperand1Value() {
			return operandValues[0];
		}
		
		/**
		 * Devuelve el valor en tiempo de ejecución del segundo operando fuente de la instrucción
		 * @return Valor en tiempo de ejecución del segundo operando fuente de la instrucción
		 */
		public double getOperand2Value() {
			return operandValues[1];			
		}
		
		/**
		 * Establece los valores de los operandos fuente de la instrucción durante el tiempo de ejecución
		 * @param values Valores de los operandos fuente
		 */
		public void setOperandValues(double[] values) {
			operandValues[0] = values[0];
			operandValues[1] = values[1];
		}
	}
}
