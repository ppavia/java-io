package fr.cimut.ged.entrant.exceptions;

public class CimutDocumentException extends GedeException {

	/**
	 * Create a new CimutDocumentException.
	 *
	 * @param message
	 *            The error or warning message.
	 */
	public CimutDocumentException(String message) {
		super(message);
	}

	/**
	 * Create a new CimutDocumentException wrapping an existing exception.
	 *
	 * <p>
	 * The existing exception will be embedded in the new one, and its message will become the default message for the
	 * CimutDocumentException.
	 * </p>
	 *
	 * @param e
	 *            The exception to be wrapped in a CimutDocumentException.
	 */
	public CimutDocumentException(Exception e) {
		super(e);
	}

	/**
	 * Create a new CimutDocumentException from an existing exception.
	 *
	 * <p>
	 * The existing exception will be embedded in the new one, but the new exception will have its own message.
	 * </p>
	 *
	 * @param message
	 *            The detail message.
	 * @param e
	 *            The exception to be wrapped in a CimutDocumentException.
	 */
	public CimutDocumentException(String message, Exception e) {
		super(message, e);
	}

}
