/**
 * 
 */
package simdeVLIWLite;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SIMDEException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2614816990257257416L;

	/**
	 * @param message
	 */
	public SIMDEException(String message) {
		super("SIMDE: " + message);
	}
}
