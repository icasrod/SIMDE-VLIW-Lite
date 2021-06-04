/**
 * 
 */
package simdeVLIWLite;

/**
 * Una instrucci�n secuencial de la m�quina simulada
 * @author Iv�n Castilla
 *
 */
public class Instruction {
	/** Identificador, igual al orden en el c�digo secuencial */
	private final int id;
	/** �ndice del bloque b�sico al que pertenece */
	private final int basicBlock;
	/** C�digo de operaci�n */
	private final Opcode opcode;
	/** Valores de los 3 operandos */
    private final int op[];
    /** Cadena de caracteres que representa cada operando */
    private final String strOp[]; 

    /**
     * Crea una instrucci�n secuencial de la m�quina simulada
     * @param id Identificador, igual al orden en el c�digo secuencial
     * @param basicBlock �ndice del bloque b�sico al que pertenece
     * @param opcode C�digo de operaci�n
     * @param op Valores de los 3 operandos
     * @param strOp Cadena de caracteres que representa cada operando
     */
	public Instruction(int id, int basicBlock, Opcode opcode, int[] op, String[] strOp) {
		this.id = id;
		this.basicBlock = basicBlock;
		this.opcode = opcode;
		this.op = op;
		this.strOp = strOp;
	}

	/**
	 * Devuelve el identificador de la instrucci�n
	 * @return Identificador, igual al orden en el c�digo secuencial
	 */
	public int getId() {
		return id;
	}

	/**
	 * Devuelve el �ndice del bloque b�sico al que pertenece
	 * @return �ndice del bloque b�sico al que pertenece
	 */
	public int getBasicBlock() {
		return basicBlock;
	}

	/**
	 * Devuelve el c�digo de operaci�n de la instrucci�n
	 * @return C�digo de operaci�n
	 */
	public Opcode getOpcode() {
		return opcode;
	}

	/**
	 * Devuelve los valores de los 3 potenciales operandos de la instrucci�n
	 * @return Valores de los 3 operandos
	 */
	public int[] getOp() {
		return op;
	}

	/**
	 * Devuelve la cadena de caracteres que representa cada operando
	 * @return Cadena de caracteres que representa cada operando
	 */
	public String[] getStrOp() {
		return strOp;
	}

	@Override
	public String toString() {
		return opcode.name() + "\t" + strOp[0] + " " + strOp[1] + " " + strOp[2]; 
	}

}
