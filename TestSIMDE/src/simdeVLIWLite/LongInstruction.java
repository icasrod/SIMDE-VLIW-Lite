/**
 * 
 */
package simdeVLIWLite;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Instrucción larga de la máquina simulada. Las instrucciones largas están compuestas de instrucciones cortas (del código secuencial original), 
 * enriquecidas con información adicional (a esta instrucción enriquecida se le llama "operación". Cada instrucción larga puede contener, como máximo, 
 * tantas instrucciones cortas como unidades funcionales se hayan definido. Como mucho, se permite una instrucción corta de salto dentro de cada instrucción larga 
 * @author Iván Castilla
 *
 */
public class LongInstruction {
	/** Una operación "nula" */ 
	public final static LongInstructionOperation NOP = new LongInstructionOperation(null, 0);
	/** Estructura interna para almacenar las instrucciones largas */
	private final TreeMap<FunctionalUnit, LongInstructionOperation[]> innerStructure;
	/** Verdadero si la instrucción larga contiene una instrucción de salto; falso en otro caso */
	private boolean isJump = false;

	/**
	 * Crea una instrucción larga vacía con la configuración indicada 
	 * @param configuration Número de unidades funcionales de cada tipo que contiene la máquina que usará estas instrucciones 
	 */
	public LongInstruction(TreeMap<FunctionalUnit, Integer> configuration) {
		this.innerStructure = new TreeMap<>();
		for (FunctionalUnit fu : FunctionalUnit.values()) {
			final LongInstructionOperation[] ops;
			// Forzando que solo haya una UF de salto
			if (FunctionalUnit.JUMP.equals(fu)) {
				ops = new LongInstructionOperation[1];
				ops[0] = NOP;
			}
			else {
				ops = new LongInstructionOperation[configuration.get(fu)];
				for (int i = 0; i < configuration.get(fu); i++)
					ops[i] = NOP;
			}
			innerStructure.put(fu, ops);
		}
	}

	/**
	 * Añade una instrucción secuencial a la instrucción larga
	 * @param inst Instrucción del código secuencial original
	 * @param idFU Identificador de la unidad funcional que la ejecutará
	 * @param pred Registro de predicado asignado a la instrucción
	 */
	public void addInstruction(Instruction inst, int idFU, int pred) {
		final LongInstructionOperation[] ops = innerStructure.get(inst.getOpcode().getFU());
		ops[idFU] = new LongInstructionOperation(inst, pred);
	}

	/**
	 * Añade una instrucción secuencial de salto a la instrucción larga
	 * @param inst Instrucción de salto del código secuencial original
	 * @param pred Registro de predicado asignado a la instrucción
	 * @param predTrue Registro de predicado que se habilita si la condición del salto resulta ser verdadera
	 * @param predFalse Registro de predicado que se habilita si la condición del salto resulta ser falsa
	 * @param destination
	 */
	public void addJumpInstruction(Instruction inst, int pred, int predTrue, int predFalse, int destination) {
		final LongInstructionOperation[] ops = innerStructure.get(inst.getOpcode().getFU());
		ops[0] = new LongInstructionJumpOperation(inst, pred, predTrue, predFalse, destination);
		isJump = true;
	}

	/**
	 * Devuelve la operación planificada para la unidad funcional especificada 
	 * @param fu Tipo de unidad funcional
	 * @param idFU Identificador de la unidad funcional
	 * @return Operación planificada para la unidad funcional especificada
	 */
	public LongInstructionOperation getOperation(FunctionalUnit fu, int idFU) {
		return innerStructure.get(fu)[idFU];
	}

	/**
	 * Devuelve las operaciones válidas (excluyendo NOP) que incluye esta instrucción larga
	 * @return las operaciones válidas (excluyendo NOP) que incluye esta instrucción larga
	 */
	public ArrayList<LongInstructionOperation> getValidOperations() {
		final ArrayList<LongInstructionOperation> list = new ArrayList<>();
		for (LongInstructionOperation[] opers : innerStructure.values()) {
			for (LongInstructionOperation op : opers) {
				if (!LongInstruction.NOP.equals(op))
					list.add(op);
			}
		}			
		return list;
	}
	
	/**
	 * Devuelve verdadero si la instrucción larga contiene un salto
	 * @return verdadero si la instrucción larga contiene un salto
	 */
	public boolean isJump() {
		return isJump;
	}

	/**
	 * Operación que encapsula una instrucción secuencial y la información adicional necesaria para planificarla dentro de una instrucción larga
	 * @author Iván Castilla
	 *
	 */
	public static class LongInstructionOperation {
		/** Registro de predicado que usa la instrucción */
		private final int pred;
		/** La instrucción a ejecutar */
		private final Instruction inst;
		/** Valores de los operandos fuente durante la ejecución */
		private final double[]operandValues;

		/**
		 * Crea una operación
		 * @param inst La instrucción a ejecutar
		 * @param pred Registro de predicado que usa la instrucción
		 */
		public LongInstructionOperation(Instruction inst, int pred) {
			super();
			this.inst = inst;
			this.pred = pred;
			this.operandValues = new double[2];
		}

		/**
		 * Devuelve la instrucción secuencial a ejecutar
		 * @return Instrucción secuencial a ejecutar
		 */
		public Instruction getInstruction() {
			return inst;
		}

		/**
		 * Devuelve el registro de predicado que usa la instrucción
		 * @return Registro de predicado que usa la instrucción
		 */
		public int getPred() {
			return pred;
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
	
	/**
	 * Una operación que encapsula una instrucción secuencial de salto
	 * @author Iván Castilla
	 *
	 */
	public static class LongInstructionJumpOperation extends LongInstructionOperation {
		 /** Registro de predicado que se activa cuando el salto es verdadero (se toma) */
		private final int predTrue;  
		 /** Registro de predicado que se activa cuando el salto es falso (no se toma) */
		private final int predFalse;
		/** Instrucción larga de destino de este salto */
		private final int destination;

		/**
		 * Crea una operación que encapsula una instrucción secuencial de salto
		 * @param inst La instrucción a ejecutar
		 * @param pred Registro de predicado que usa la instrucción
		 * @param predTrue Registro de predicado que se activa cuando el salto es verdadero (se toma)
		 * @param predFalse Registro de predicado que se activa cuando el salto es falso (no se toma)
		 * @param destination Instrucción larga de destino de este salto
		 */
		public LongInstructionJumpOperation(Instruction inst, int pred, int predTrue, int predFalse, int destination) {
			super(inst, pred);
			this.predTrue = predTrue;
			this.predFalse = predFalse;
			this.destination = destination;
		}

		/**
		 * Devuelve el registro de predicado que se activa cuando el salto es verdadero (se toma)
		 * @return Registro de predicado que se activa cuando el salto es verdadero (se toma)
		 */
		public int getPredTrue() {
			return predTrue;
		}

		/**
		 * Devuelve el registro de predicado que se activa cuando el salto es falso (no se toma)
		 * @return Registro de predicado que se activa cuando el salto es falso (no se toma)
		 */
		public int getPredFalse() {
			return predFalse;
		}

		/**
		 * Devuelve la instrucción larga de destino de este salto
		 * @return Instrucción larga de destino de este salto
		 */
		public int getDestination() {
			return destination;
		}
		
	}
}
