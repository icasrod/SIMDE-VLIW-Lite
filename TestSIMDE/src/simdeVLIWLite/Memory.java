/**
 * 
 */
package simdeVLIWLite;

import java.util.Random;
import java.util.TreeMap;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class Memory {
	public final static String STR = "#MEM";
	private final TreeMap<Integer, Double> innerStructure;
	private final int size;
	private final double cacheMissRate;
	private final int cacheMissPenalty;
	private final Random rnd;

	/**
	 * 
	 */
	public Memory(int size, double cacheMissRate, int cacheMissPenalty) {
		this.size = size;
		this.innerStructure = new TreeMap<>();
		this.cacheMissRate = cacheMissRate;
		this.cacheMissPenalty = cacheMissPenalty;
		this.rnd = new Random();
	}

	public void write(int address, double value) throws SIMDEException {
		if (address < 0 || address >= size)
			throw new SIMDEException("Direcci�n de memoria inv�lida (" + address + ")");
		innerStructure.put(address, value);
	}
	
	public double read(int address) throws SIMDEException {
		if (address < 0 || address >= size)
			throw new SIMDEException("Direcci�n de memoria inv�lida (" + address + ")");
		if (innerStructure.containsKey(address))
			return innerStructure.get(address);
		return 0;
	}
	
	/**
	 * Devuelve verdadero si este acceso producir� un fallo de cach�. Por motivos de simulaci�n se pregunta antes de 
	 * hacer el acceso en s�.
	 * @return verdadero si este acceso producir� un fallo de cach�.
	 */
	public boolean willProduceCacheMiss() {
		return (rnd.nextDouble() < cacheMissRate);
	}
	
	public void reset() {
		innerStructure.clear();
	}
	
	/**
	 * @return the cacheFailRate
	 */
	public double getCacheMissRate() {
		return cacheMissRate;
	}

	/**
	 * @return the cacheFailPenalty
	 */
	public int getCacheMissPenalty() {
		return cacheMissPenalty;
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
