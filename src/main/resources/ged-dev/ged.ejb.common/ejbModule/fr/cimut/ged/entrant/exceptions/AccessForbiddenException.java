package fr.cimut.ged.entrant.exceptions;

import org.apache.commons.httpclient.HttpStatus;

public class AccessForbiddenException extends GedeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5004380087875301317L;

	/**
	 * Create a new CimutFileException.
	 *
	 * @param message
	 *            The error or warning message.
	 */
	public AccessForbiddenException() {
		super("access Forbidden");
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.SC_FORBIDDEN;
	}

}
