/**
 * 
 */
package simdeVLIWLite;

/**
 * Unidades funcionales de que dispone la máquina
 * @author Iván Castilla
 *
 */
public enum FunctionalUnit {
	INT_ADD(1),
	INT_MULT(2),
	FP_ADD(4),
	FP_MULT(6),
	MEM(4),
	JUMP(2);
	
	/**
	 * Crea una undad funcional de latencia "latency"
	 * @param latency Latencia de la unidad funcional, es decir, cuántos ciclos tarda en ejecutar una instrucción
	 */
	private FunctionalUnit(int latency) {
		this.latency = latency;
	}
	
	/** Latencia de la unidad funcional, es decir, cuántos ciclos tarda en ejecutar una instrucción */
	private final int latency;

	/**
	 * Devuelve la latencia de la unidad funcional, es decir, cuántos ciclos tarda en ejecutar una instrucción
	 * @return la latencia de la unidad funcional, es decir, cuántos ciclos tarda en ejecutar una instrucción
	 */
	public int getDefaultLatency() {
		return latency;
	}
}
