/**
 * 
 */
package fr.cimut.ged.entrant.exceptions;

public class CimutMongoDBException extends GedeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 435129388246556256L;

	/**
	 * @param message
	 */
	public CimutMongoDBException(final String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CimutMongoDBException(final Throwable cause) {
		this(null, cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CimutMongoDBException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
