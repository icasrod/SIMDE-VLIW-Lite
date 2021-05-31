/**
 * 
 */
package simdeVLIWLite;

/**
 * @author Iván Castilla
 *
 */
public enum OperandType {
	INMEDIATE {
		@Override
		int[] assemble(String op) {
			return new int[] {Integer.parseInt(op.substring(1))};
		}
	},
	GPREGISTER {
		@Override
		int[] assemble(String op) {
			return new int[] {Integer.parseInt(op.substring(1))};
		}
	},
	FPREGISTER {
		@Override
		int[] assemble(String op) {
			return new int[] {Integer.parseInt(op.substring(1))};
		}
	},
	LABEL {
		@Override
		int[] assemble(String op) {
			// El valor de la etiqueta se calcula al final
			return new int[] {-1};
		}
	},
	ADDRESS {
		@Override
		int[] assemble(String op) {
			final int[] ops = new int[2];
			int pos = op.indexOf('(');
			ops[0] = (pos == 0) ? 0 : Integer.parseInt(op.substring(0, pos));
			ops[1] = Integer.parseInt(op.substring(pos + 2, op.length() - 1));
			return ops;
		}
	};
	
	/**
	 * Crea el o los operandos en bajo nivel para la instrucción
	 * @return el o los operandos en bajo nivel para la instrucción
	 */
	abstract int[] assemble(String op);
}
