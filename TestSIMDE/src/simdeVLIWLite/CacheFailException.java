/**
 * 
 */
package simdeVLIWLite;

/**
 * @author Iván Castilla
 *
 */
public class CacheFailException extends SIMDEException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5156892496047839658L;

	/**
	 * @param message
	 */
	public CacheFailException() {
		super("Fallo de caché");
	}

}
