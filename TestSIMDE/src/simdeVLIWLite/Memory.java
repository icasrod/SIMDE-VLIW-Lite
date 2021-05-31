/**
 * 
 */
package simdeVLIWLite;

import java.util.TreeMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Memory {
	public final static String STR = "#MEM";
	private final TreeMap<Integer, Double> innerStructure;
	private final int size;

	/**
	 * 
	 */
	public Memory(int size) {
		this.size = size;
		this.innerStructure = new TreeMap<>();
	}

	public void write(int address, double value) throws SIMDEException {
		if (address < 0 || address >= size)
			throw new SIMDEException("Invalid memory address (" + address + ")");
		innerStructure.put(address, value);
	}
	
	public double read(int address) throws SIMDEException {
		if (address < 0 || address >= size)
			throw new SIMDEException("Invalid memory address (" + address + ")");
		if (innerStructure.containsKey(address))
			return innerStructure.get(address);
		return 0;
	}
	
	@Override
	public String toString() {
		final StringBuffer str = new StringBuffer(STR + System.lineSeparator());
		for (Integer dir : innerStructure.keySet()) {
			str.append("[" + dir + "] " + innerStructure.get(dir) + System.lineSeparator());
		}
		return str.toString();
	}
	
	
}
