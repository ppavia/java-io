package fr.cimut.ged.entrant.exceptions;

import org.apache.commons.httpclient.HttpStatus;

/**
 * l'exception Gede parente de toutes les exception jetées par la gede
 * 
 * @author jlebourgocq
 *
 */
public abstract class GedeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5562201585586401871L;

	/**
	 * @serial The embedded exception if tunnelling, or null.
	 */
	protected final Throwable exception;

	private int httpErrorCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;

	/**
	 * Create a new CimutFileException.
	 *
	 * @param message
	 *            The error or warning message.
	 */
	public GedeException(String message) {
		super(message);
		exception = null;
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
	public GedeException(Exception e) {
		super();
		this.exception = e;
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
	public GedeException(String message, Throwable e) {
		super(message);
		this.exception = e;
	}

	/**
	 * Return a detail message for this exception.
	 *
	 * <p>
	 * If there is an embedded exception, and if the CimutFileException has no detail message of its own, this method
	 * will return the detail message from the embedded exception.
	 * </p>
	 *
	 * @return The error or warning message.
	 */
	@Override
	public String getMessage() {
		String message = super.getMessage();

		if (message == null && exception != null) {
			return exception.getMessage();
		} else {
			return message;
		}
	}

	/**
	 * Return the embedded exception, if any.
	 *
	 * @return The embedded exception, or null if there is none.
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Override toString to pick up any embedded exception.
	 *
	 * @return A string representation of this exception.
	 */
	@Override
	public String toString() {
		if (exception != null) {
			return exception.toString();
		} else {
			return super.toString();
		}
	}

	public void setHttpErrorCode(int httpErrorCode) {
		this.httpErrorCode = httpErrorCode;
	}

	public int getHttpErrorCode() {
		return httpErrorCode;
	}

	public GedeError toGedeError() {
		GedeError error = new GedeError();
		error.setCode(this.getClass().getSimpleName());
		error.setMessage(this.getMessage());
		return error;
	}

}
