/**
 * 
 */
package simdeVLIWLite;

/**
 * Una excepci�n que puede ser un error de ejecuci�n de la m�quina simulada
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SIMDEException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2614816990257257416L;

	/**
	 * Crea una excepci�n 
	 * @param message Mensaje de error
	 */
	public SIMDEException(String message) {
		super("SIMDE: " + message);
	}
}
