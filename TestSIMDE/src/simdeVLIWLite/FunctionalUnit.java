/**
 * 
 */
package simdeVLIWLite;

/**
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
	
	private FunctionalUnit(int latency) {
		this.latency = latency;
	}
	
	private int latency;

	/**
	 * @return the latency
	 */
	public int getLatency() {
		return latency;
	}

	/**
	 * @param latency the latency to set
	 */
	public void setLatency(int latency) {
		this.latency = latency;
	}
}
