package fr.cimut.ged.entrant.exceptions;

public class CimutMailBlackListedException extends GedeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7852374942089217137L;

	/**
	 * Create a new CimutFileException.
	 *
	 * @param message
	 *            The error or warning message.
	 */
	public CimutMailBlackListedException(String message) {
		super(message);
	}

	/**
	 * Create a new CimutFileException wrapping an existing exception.
	 *
	 * <p>
	 * The existing exception will be embedded in the new one, and its message will become the default message for the
	 * CimutFileException.
	 * </p>
	 *
	 * @param e
	 *            The exception to be wrapped in a CimutFileException.
	 */
	public CimutMailBlackListedException(Exception e) {
		super(e);
	}

	/**
	 * Create a new CimutFileException from an existing exception.
	 *
	 * <p>
	 * The existing exception will be embedded in the new one, but the new exception will have its own message.
	 * </p>
	 *
	 * @param message
	 *            The detail message.
	 * @param e
	 *            The exception to be wrapped in a CimutFileException.
	 */
	public CimutMailBlackListedException(String message, Exception e) {
		super(message, e);
	}

}
