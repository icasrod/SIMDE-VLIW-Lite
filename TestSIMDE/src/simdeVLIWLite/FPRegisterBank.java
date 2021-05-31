/**
 * 
 */
package simdeVLIWLite;

import java.util.TreeMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class FPRegisterBank {
	public final static String STR = "#FPR";
	private final TreeMap<Integer, Double> innerStructure;
	private final int size;

	/**
	 * 
	 */
	public FPRegisterBank(int size) {
		this.size = size;
		this.innerStructure = new TreeMap<>();
	}

	public void write(int index, double value) {
		innerStructure.put(index, value);
	}
	
	public double read(int index) {
		if (innerStructure.containsKey(index))
			return innerStructure.get(index);
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
