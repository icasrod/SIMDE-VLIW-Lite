/**
 * 
 */
package simdeVLIWLite;

import java.util.TreeMap;

/**
 * Banco de registros de punto flotante
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class FPRegisterBank {
	/** Identificador en fichero de la parte que describe el contenido de estos registros */
	public final static String STR = "#FPR";
	/** Estructura interna que almacena los valores de los registros */
	private final TreeMap<Integer, Double> innerStructure;
	/** N�mero de registros que contiene el banco */
	private final int size;

	/**
	 * Crea un banco de registros de punto flotante de tama�o "size"
	 * @param size Tama�o del banco de registros creado
	 */
	public FPRegisterBank(int size) {
		this.size = size;
		this.innerStructure = new TreeMap<>();
	}

	/**
	 * Escribe un valor en un registro.
	 * No realiza control de errores: asume que el �ndice es v�lido
	 * @param index �ndice del registro en el que hay que escribir el valor
	 * @param value Valor a escribir
	 */
	public void write(int index, double value) {
		innerStructure.put(index, value);
	}
	
	/**
	 * Lee el valor de un registro
	 * Asume que todos los registros se inicializan a 0.
	 * No realiza control de errores: asume que el �ndice es v�lido
	 * @param index �ndice del registro del que hay que leer el valor
	 * @return El valor del registro que ocupa la posici�n "index"
	 */
	public double read(int index) {
		if (innerStructure.containsKey(index))
			return innerStructure.get(index);
		return 0;
	}
	
	/**
	 * Resetea el banco de registros, eliminando cualquier asignaci�n de valor que se haya realizado
	 */
	public void reset() {
		innerStructure.clear();
	}
	
	/**
	 * Devuelve el n�mero de registros que tiene el banco
	 * @return n�mero de registros que tiene el banco
	 */
	public int getSize() {
		return size;
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
