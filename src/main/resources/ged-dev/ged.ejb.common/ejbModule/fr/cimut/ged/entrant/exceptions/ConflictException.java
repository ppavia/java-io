package fr.cimut.ged.entrant.exceptions;

import org.apache.commons.httpclient.HttpStatus;

public class ConflictException extends GedeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 614355471365403555L;

	public ConflictException(String message) {
		super(message);
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.SC_CONFLICT;
	}
}
