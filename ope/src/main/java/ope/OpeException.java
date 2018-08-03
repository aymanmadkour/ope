package ope;

/**
 * General OPE exception
 * 
 * @author Ayman Madkour <info@aymanmadkour.com>
 */
public class OpeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OpeException(String message) {
		super(message);
	}

	public OpeException(String message, Throwable cause) {
		super(message, cause);
	}
}
