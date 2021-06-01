/**
 * 
 */
package simdeVLIWLite;

import java.util.TreeMap;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PredicateRegisterBank {
	public final static String STR = "#PRED";
	private final TreeMap<Integer, Boolean> innerStructure;
	private final int size;

	/**
	 * 
	 */
	public PredicateRegisterBank(int size) {
		this.size = size;
		this.innerStructure = new TreeMap<>();
	}

	public void write(int index, boolean value) {
		// Para prevenir modificar el registro 0
		if (index > 0)
			innerStructure.put(index, value);
	}
	
	public boolean read(int index) {
		if (innerStructure.containsKey(index))
			return innerStructure.get(index);
		return (index==0);
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
