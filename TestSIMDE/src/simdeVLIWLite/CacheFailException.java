/**
 * 
 */
package simdeVLIWLite;

/**
 * Excepci�n de la m�quina que se produce al ocurrir un fallo de cach�
 * @author Iv�n Castilla
 *
 */
public class CacheFailException extends SIMDEException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5156892496047839658L;

	/**
	 * Crea una excepci�n de fallo de cach�
	 */
	public CacheFailException() {
		super("Fallo de cach�");
	}

}
