/**
 * 
 */
package simdeVLIWLite;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author Iván Castilla
 *
 */
public class LongInstruction {
	private final TreeMap<FunctionalUnit, LongInstructionOperation[]> innerStructure;
	public final static LongInstructionOperation NOP = new LongInstructionOperation(null, 0);
	private boolean isJump = false;
	/**
	 * 
	 */
	public LongInstruction(TreeMap<FunctionalUnit, Integer> configuration) {
		this.innerStructure = new TreeMap<>();
		for (FunctionalUnit fu : FunctionalUnit.values()) {
			final LongInstructionOperation[] ops;
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

	public void addInstruction(Instruction inst, int idFU, int pred) {
		final LongInstructionOperation[] ops = innerStructure.get(inst.getOpcode().getFU());
		ops[idFU] = new LongInstructionOperation(inst, pred);
	}

	public void addJumpInstruction(Instruction inst, int pred, int predTrue, int predFalse, int destination) {
		final LongInstructionOperation[] ops = innerStructure.get(inst.getOpcode().getFU());
		ops[0] = new LongInstructionJumpOperation(inst, pred, predTrue, predFalse, destination);
		isJump = true;
	}

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
	 * @return the isJump
	 */
	public boolean isJump() {
		return isJump;
	}

	public static class LongInstructionOperation {
		/** Registro de predicado que usa la instrucción */
		private final int pred;
		/** La instrucción a ejecutar */
		private final Instruction inst;
		private final double[]operandValues;
		
		/**
		 * @param fuType
		 * @param idFU
		 * @param pred
		 */
		public LongInstructionOperation(Instruction inst, int pred) {
			super();
			this.inst = inst;
			this.pred = pred;
			this.operandValues = new double[2];
		}

		/**
		 * @return the instruction
		 */
		public Instruction getInstruction() {
			return inst;
		}

		/**
		 * @return the pred
		 */
		public int getPred() {
			return pred;
		}
		
		public double getOperand1Value() {
			return operandValues[0];
		}
		
		public double getOperand2Value() {
			return operandValues[1];			
		}
		
		public void setOperandValues(double[] values) {
			operandValues[0] = values[0];
			operandValues[1] = values[1];
		}
	}
	
	public static class LongInstructionJumpOperation extends LongInstructionOperation {
		 /** Registro de predicado que se activa cuando el salto es verdadero (se toma) */
		private final int predTrue;  
		 /** Registro de predicado que se activa cuando el salto es falso (no se toma) */
		private final int predFalse;
		/** Instrucción larga de destino de este salto */
		private final int destination;

		public LongInstructionJumpOperation(Instruction inst, int pred, int predTrue, int predFalse, int destination) {
			super(inst, pred);
			this.predTrue = predTrue;
			this.predFalse = predFalse;
			this.destination = destination;
		}

		/**
		 * @return the predTrue
		 */
		public int getPredTrue() {
			return predTrue;
		}

		/**
		 * @return the predFalse
		 */
		public int getPredFalse() {
			return predFalse;
		}

		/**
		 * @return the destination
		 */
		public int getDestination() {
			return destination;
		}
		
	}
}
