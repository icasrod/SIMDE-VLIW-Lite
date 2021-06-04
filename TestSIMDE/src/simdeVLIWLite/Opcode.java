/**
 * 
 */
package simdeVLIWLite;

/**
 * Códigos de operación permitidos para las instrucciones secuenciales de la máquina simulada
 * @author Iván Castilla
 *
 */
public enum Opcode {
	ADD(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}),
	ADDI(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.INMEDIATE}), 
	SUB(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}),
	OR(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}), 
	AND(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}), 
	XOR(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}), 
	NOR(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}), 
	SLLV(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}), 
	SRLV(FunctionalUnit.INT_ADD, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}), 
	ADDF(FunctionalUnit.FP_ADD, new OperandType[] {OperandType.FPREGISTER, OperandType.FPREGISTER, OperandType.FPREGISTER}), 
	SUBF(FunctionalUnit.FP_ADD, new OperandType[] {OperandType.FPREGISTER, OperandType.FPREGISTER, OperandType.FPREGISTER}), 
	MULT(FunctionalUnit.INT_MULT, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.GPREGISTER}), 
	MULTF(FunctionalUnit.FP_MULT, new OperandType[] {OperandType.FPREGISTER, OperandType.FPREGISTER, OperandType.FPREGISTER}), 
	SW(FunctionalUnit.MEM, new OperandType[] {OperandType.GPREGISTER, OperandType.ADDRESS}), 
	SF(FunctionalUnit.MEM, new OperandType[] {OperandType.FPREGISTER, OperandType.ADDRESS}), 
	LW(FunctionalUnit.MEM, new OperandType[] {OperandType.GPREGISTER, OperandType.ADDRESS}), 
	LF(FunctionalUnit.MEM, new OperandType[] {OperandType.FPREGISTER, OperandType.ADDRESS}), 
	BNE(FunctionalUnit.JUMP, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.LABEL}), 
	BEQ(FunctionalUnit.JUMP, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.LABEL}), 
	BGT(FunctionalUnit.JUMP, new OperandType[] {OperandType.GPREGISTER, OperandType.GPREGISTER, OperandType.LABEL});
	
	/** Unidad funcional donde se ejecuta esta instrucción */
	private final FunctionalUnit fu;
	/** Operandos que define la instrucción */
	private final OperandType[] operands;
	
	/**
	 * Crea un código de operación
	 * @param fu Unidad funcional donde se ejecuta esta instrucción
	 * @param operands Operandos que define la instrucción
	 */
	private Opcode(FunctionalUnit fu, OperandType[] operands) {
		this.fu = fu;
		this.operands = operands;
	}
	
	/**
	 * Devuelve la unidad funcional donde se ejecuta esta instrucción
	 * @return Unidad funcional donde se ejecuta esta instrucción
	 */
	public FunctionalUnit getFU() {
		return fu;
	}

	/**
	 * Devuelve los operandos que define la instrucción
	 * @return Operandos que define la instrucción
	 */
	public OperandType[] getOperands() {
		return operands;
	}
}
