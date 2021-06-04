/**
 * 
 */
package simdeVLIWLite;

import java.util.TreeMap;

/**
 * Banco de registros de predicado
 * @author Iván Castilla Rodríguez
 *
 */
public class PredicateRegisterBank {
	/** Identificador en fichero de la parte que describe el contenido de estos registros (no usado) */
	public final static String STR = "#PRED";
	/** Estructura interna que almacena los valores de los registros */
	private final TreeMap<Integer, Boolean> innerStructure;
	/** Número de registros que contiene el banco */
	private final int size;

	/**
	 * Crea un banco de registros de predicado de tamaño "size"
	 * @param size Tamaño del banco de registros creado
	 */
	public PredicateRegisterBank(int size) {
		this.size = size;
		this.innerStructure = new TreeMap<>();
	}

	/**
	 * Escribe un valor en un registro.
	 * Ignora las escrituras al registro 0.
	 * No realiza control de errores: asume que el índice es válido
	 * @param index Índice del registro en el que hay que escribir el valor
	 * @param value Valor a escribir
	 */
	public void write(int index, boolean value) {
		// Para prevenir modificar el registro 0
		if (index > 0)
			innerStructure.put(index, value);
	}
	
	/**
	 * Lee el valor de un registro
	 * Asume que todos los registros se inicializan a 1 (verdadero).
	 * No realiza control de errores: asume que el índice es válido
	 * @param index Índice del registro del que hay que leer el valor
	 * @return El valor del registro que ocupa la posición "index"
	 */
	public boolean read(int index) {
		if (innerStructure.containsKey(index))
			return innerStructure.get(index);
		return (index==0);
	}
	
	/**
	 * Resetea el banco de registros, eliminando cualquier asignación de valor que se haya realizado
	 */
	public void reset() {
		innerStructure.clear();
	}
	
	/**
	 * Devuelve el número de registros que tiene el banco
	 * @return número de registros que tiene el banco
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
