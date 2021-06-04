/**
 * 
 */
package simdeVLIWLite;

/**
 * Una excepción que puede ser un error de ejecución de la máquina simulada
 * @author Iván Castilla Rodríguez
 *
 */
public class SIMDEException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2614816990257257416L;

	/**
	 * Crea una excepción 
	 * @param message Mensaje de error
	 */
	public SIMDEException(String message) {
		super("SIMDE: " + message);
	}
}
