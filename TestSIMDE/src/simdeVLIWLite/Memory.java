/**
 * 
 */
package simdeVLIWLite;

import java.util.TreeMap;

/**
 * Crea una estructura de memoria de datos muy simple que incluye una caché con una tasa de 
 * fallos aleatoria.
 * @author Iván Castilla Rodríguez
 *
 */
public class Memory {
	/** Identificador en fichero de la parte que describe el contenido de esta memoria */
	public final static String STR = "#MEM";
	/** Estructura interna que almacena los valores de la memoria */
	private final TreeMap<Integer, Double> innerStructure;
	/** Número de palabras que contiene la memoria principal */
	private final int size;
	/** Tasa de fallos de la caché, expresada en tanto por 1 */
	private final double cacheMissRate;
	/** Penalización en ciclos si se produce un fallo de la caché */
	private final int cacheMissPenalty;

	/**
	 * Crea una memoria simple
	 * @param size Número de palabras de la memoria
	 * @param cacheMissRate Tasa de fallos de la caché, expresada como un valor entre 0 y 100 (%).
	 * @param cacheMissPenalty Penalización en ciclos si se produce un fallo de la caché
	 */
	public Memory(int size, double cacheMissRate, int cacheMissPenalty) {
		this.size = size;
		this.innerStructure = new TreeMap<>();
		this.cacheMissRate = cacheMissRate;
		this.cacheMissPenalty = cacheMissPenalty;
	}

	/**
	 * Escribe un valor en la memoria
	 * @param address Dirección donde se realiza la escritura
	 * @param value Valor a escribir
	 * @throws SIMDEException Excepción en caso de que se intente acceder a una dirección no válida
	 */
	public void write(int address, double value) throws SIMDEException {
		if (address < 0 || address >= size)
			throw new SIMDEException("Dirección de memoria inválida (" + address + ")");
		innerStructure.put(address, value);
	}

	/**
	 * Lee un valor de la memoria
	 * @param address Dirección donde se realiza la lectura
	 * @return El valor leído
	 * @throws SIMDEException Excepción en caso de que se intente acceder a una dirección no válida
	 */
	public double read(int address) throws SIMDEException {
		if (address < 0 || address >= size)
			throw new SIMDEException("Dirección de memoria inválida (" + address + ")");
		if (innerStructure.containsKey(address))
			return innerStructure.get(address);
		return 0;
	}
	
	/**
	 * Vacía el contenido de la memoria
	 */
	public void reset() {
		innerStructure.clear();
	}
	
	/**
	 * Devuelve la tasa de fallos de la caché en tanto por uno
	 * @return tasa de fallos de la caché en tanto por uno
	 */
	public double getCacheMissRate() {
		return cacheMissRate;
	}

	/**
	 * Devuelve la penalización en ciclos de un fallo de caché
	 * @return penalización en ciclos de un fallo de caché
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
