/**
 * 
 */
package simdeVLIWLite;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Instrucci�n larga de la m�quina simulada. Las instrucciones largas est�n compuestas de instrucciones cortas (del c�digo secuencial original), 
 * enriquecidas con informaci�n adicional (a esta instrucci�n enriquecida se le llama "operaci�n". Cada instrucci�n larga puede contener, como m�ximo, 
 * tantas instrucciones cortas como unidades funcionales se hayan definido. Como mucho, se permite una instrucci�n corta de salto dentro de cada instrucci�n larga 
 * @author Iv�n Castilla
 *
 */
public class LongInstruction {
	/** Una operaci�n "nula" */ 
	public final static LongInstructionOperation NOP = new LongInstructionOperation(null, 0);
	/** Estructura interna para almacenar las instrucciones largas */
	private final TreeMap<FunctionalUnit, LongInstructionOperation[]> innerStructure;
	/** Verdadero si la instrucci�n larga contiene una instrucci�n de salto; falso en otro caso */
	private boolean isJump = false;

	/**
	 * Crea una instrucci�n larga vac�a con la configuraci�n indicada 
	 * @param configuration N�mero de unidades funcionales de cada tipo que contiene la m�quina que usar� estas instrucciones 
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
	 * A�ade una instrucci�n secuencial a la instrucci�n larga
	 * @param inst Instrucci�n del c�digo secuencial original
	 * @param idFU Identificador de la unidad funcional que la ejecutar�
	 * @param pred Registro de predicado asignado a la instrucci�n
	 */
	public void addInstruction(Instruction inst, int idFU, int pred) {
		final LongInstructionOperation[] ops = innerStructure.get(inst.getOpcode().getFU());
		ops[idFU] = new LongInstructionOperation(inst, pred);
	}

	/**
	 * A�ade una instrucci�n secuencial de salto a la instrucci�n larga
	 * @param inst Instrucci�n de salto del c�digo secuencial original
	 * @param pred Registro de predicado asignado a la instrucci�n
	 * @param predTrue Registro de predicado que se habilita si la condici�n del salto resulta ser verdadera
	 * @param predFalse Registro de predicado que se habilita si la condici�n del salto resulta ser falsa
	 * @param destination
	 */
	public void addJumpInstruction(Instruction inst, int pred, int predTrue, int predFalse, int destination) {
		final LongInstructionOperation[] ops = innerStructure.get(inst.getOpcode().getFU());
		ops[0] = new LongInstructionJumpOperation(inst, pred, predTrue, predFalse, destination);
		isJump = true;
	}

	/**
	 * Devuelve la operaci�n planificada para la unidad funcional especificada 
	 * @param fu Tipo de unidad funcional
	 * @param idFU Identificador de la unidad funcional
	 * @return Operaci�n planificada para la unidad funcional especificada
	 */
	public LongInstructionOperation getOperation(FunctionalUnit fu, int idFU) {
		return innerStructure.get(fu)[idFU];
	}

	/**
	 * Devuelve las operaciones v�lidas (excluyendo NOP) que incluye esta instrucci�n larga
	 * @return las operaciones v�lidas (excluyendo NOP) que incluye esta instrucci�n larga
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
	 * Devuelve verdadero si la instrucci�n larga contiene un salto
	 * @return verdadero si la instrucci�n larga contiene un salto
	 */
	public boolean isJump() {
		return isJump;
	}

	/**
	 * Operaci�n que encapsula una instrucci�n secuencial y la informaci�n adicional necesaria para planificarla dentro de una instrucci�n larga
	 * @author Iv�n Castilla
	 *
	 */
	public static class LongInstructionOperation {
		/** Registro de predicado que usa la instrucci�n */
		private final int pred;
		/** La instrucci�n a ejecutar */
		private final Instruction inst;
		/** Valores de los operandos fuente durante la ejecuci�n */
		private final double[]operandValues;

		/**
		 * Crea una operaci�n
		 * @param inst La instrucci�n a ejecutar
		 * @param pred Registro de predicado que usa la instrucci�n
		 */
		public LongInstructionOperation(Instruction inst, int pred) {
			super();
			this.inst = inst;
			this.pred = pred;
			this.operandValues = new double[2];
		}

		/**
		 * Devuelve la instrucci�n secuencial a ejecutar
		 * @return Instrucci�n secuencial a ejecutar
		 */
		public Instruction getInstruction() {
			return inst;
		}

		/**
		 * Devuelve el registro de predicado que usa la instrucci�n
		 * @return Registro de predicado que usa la instrucci�n
		 */
		public int getPred() {
			return pred;
		}
		
		/**
		 * Devuelve el valor en tiempo de ejecuci�n del primer operando fuente de la instrucci�n
		 * @return Valor en tiempo de ejecuci�n del primer operando fuente de la instrucci�n
		 */
		public double getOperand1Value() {
			return operandValues[0];
		}
		
		/**
		 * Devuelve el valor en tiempo de ejecuci�n del segundo operando fuente de la instrucci�n
		 * @return Valor en tiempo de ejecuci�n del segundo operando fuente de la instrucci�n
		 */
		public double getOperand2Value() {
			return operandValues[1];			
		}
		
		/**
		 * Establece los valores de los operandos fuente de la instrucci�n durante el tiempo de ejecuci�n
		 * @param values Valores de los operandos fuente
		 */
		public void setOperandValues(double[] values) {
			operandValues[0] = values[0];
			operandValues[1] = values[1];
		}
	}
	
	/**
	 * Una operaci�n que encapsula una instrucci�n secuencial de salto
	 * @author Iv�n Castilla
	 *
	 */
	public static class LongInstructionJumpOperation extends LongInstructionOperation {
		 /** Registro de predicado que se activa cuando el salto es verdadero (se toma) */
		private final int predTrue;  
		 /** Registro de predicado que se activa cuando el salto es falso (no se toma) */
		private final int predFalse;
		/** Instrucci�n larga de destino de este salto */
		private final int destination;

		/**
		 * Crea una operaci�n que encapsula una instrucci�n secuencial de salto
		 * @param inst La instrucci�n a ejecutar
		 * @param pred Registro de predicado que usa la instrucci�n
		 * @param predTrue Registro de predicado que se activa cuando el salto es verdadero (se toma)
		 * @param predFalse Registro de predicado que se activa cuando el salto es falso (no se toma)
		 * @param destination Instrucci�n larga de destino de este salto
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
		 * Devuelve la instrucci�n larga de destino de este salto
		 * @return Instrucci�n larga de destino de este salto
		 */
		public int getDestination() {
			return destination;
		}
		
	}
}
