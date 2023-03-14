package fr.cimut.ged.entrant.exceptions;

public class CimutXmlException extends GedeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Create a new CimutXmlException.
	 *
	 * @param message
	 *            The error or warning message.
	 */
	public CimutXmlException(String message) {
		super(message);
	}

	/**
	 * Create a new CimutXmlException wrapping an existing exception.
	 *
	 * <p>
	 * The existing exception will be embedded in the new one, and its message will become the default message for the
	 * CimutXmlException.
	 * </p>
	 *
	 * @param e
	 *            The exception to be wrapped in a CimutXmlException.
	 */
	public CimutXmlException(Exception e) {
		super(e);
	}

	/**
	 * Create a new CimutXmlException from an existing exception.
	 *
	 * <p>
	 * The existing exception will be embedded in the new one, but the new exception will have its own message.
	 * </p>
	 *
	 * @param message
	 *            The detail message.
	 * @param e
	 *            The exception to be wrapped in a CimutXmlException.
	 */
	public CimutXmlException(String message, Exception e) {
		super(message, e);
	}

}
