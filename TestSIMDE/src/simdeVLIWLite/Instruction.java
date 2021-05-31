/**
 * 
 */
package simdeVLIWLite;

/**
 * @author Iván Castilla
 *
 */
public class Instruction {
	/** Identificador, igual al orden en el código secuencial */
	private final int id;
	/** Índice del bloque básico al que pertenece */
	private final int basicBlock;
	/** Código de operación */
	private final Opcode opcode;
	/** Valores de los 3 operandos */
    private final int op[];
    private final String strOpcode;   // Cadena de caracteres con el opcode
    private final String strOp[];    // Cadena de caracteres que representa cada operando

    /**
	 * @param id
	 * @param basicBlock
	 * @param opcode
	 * @param op
	 * @param strOpcode
	 * @param strOp
	 * @param strLabel
	 */
	public Instruction(int id, int basicBlock, Opcode opcode, int[] op, String strOpcode, String[] strOp) {
		this.id = id;
		this.basicBlock = basicBlock;
		this.opcode = opcode;
		this.op = op;
		this.strOpcode = strOpcode;
		this.strOp = strOp;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the bBasico
	 */
	public int getBasicBlock() {
		return basicBlock;
	}

	/**
	 * @return the opcode
	 */
	public Opcode getOpcode() {
		return opcode;
	}

	/**
	 * @return the op
	 */
	public int[] getOp() {
		return op;
	}

	/**
	 * @return the strOpcode
	 */
	public String getStrOpcode() {
		return strOpcode;
	}

	/**
	 * @return the strOp
	 */
	public String[] getStrOp() {
		return strOp;
	}

	@Override
	public String toString() {
		return strOpcode + "\t" + strOp[0] + " " + strOp[1] + " " + strOp[2]; 
	}

}
