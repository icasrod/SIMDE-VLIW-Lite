/**
 * 
 */
package simdeVLIWLite;

import java.util.TreeMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class GPRegisterBank {
	public final static String STR = "#GPR";
	private final TreeMap<Integer, Integer> innerStructure;
	private final int size;

	/**
	 * 
	 */
	public GPRegisterBank(int size) {
		this.size = size;
		this.innerStructure = new TreeMap<>();
	}

	public void write(int index, int value) {
		// Para prevenir modificar el registro 0
		if (index > 0)
			innerStructure.put(index, value);
	}
	
	public int read(int index) {
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
