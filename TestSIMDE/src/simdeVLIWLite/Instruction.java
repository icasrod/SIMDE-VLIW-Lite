/**
 * 
 */
package simdeVLIWLite;

/**
 * Una instrucción secuencial de la máquina simulada
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
    /** Cadena de caracteres que representa cada operando */
    private final String strOp[]; 

    /**
     * Crea una instrucción secuencial de la máquina simulada
     * @param id Identificador, igual al orden en el código secuencial
     * @param basicBlock Índice del bloque básico al que pertenece
     * @param opcode Código de operación
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
	 * Devuelve el identificador de la instrucción
	 * @return Identificador, igual al orden en el código secuencial
	 */
	public int getId() {
		return id;
	}

	/**
	 * Devuelve el índice del bloque básico al que pertenece
	 * @return Índice del bloque básico al que pertenece
	 */
	public int getBasicBlock() {
		return basicBlock;
	}

	/**
	 * Devuelve el código de operación de la instrucción
	 * @return Código de operación
	 */
	public Opcode getOpcode() {
		return opcode;
	}

	/**
	 * Devuelve los valores de los 3 potenciales operandos de la instrucción
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
