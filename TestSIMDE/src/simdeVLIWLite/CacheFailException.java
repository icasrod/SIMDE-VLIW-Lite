/**
 * 
 */
package simdeVLIWLite;

/**
 * Excepción de la máquina que se produce al ocurrir un fallo de caché
 * @author Iván Castilla
 *
 */
public class CacheFailException extends SIMDEException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5156892496047839658L;

	/**
	 * Crea una excepción de fallo de caché
	 */
	public CacheFailException() {
		super("Fallo de caché");
	}

}
