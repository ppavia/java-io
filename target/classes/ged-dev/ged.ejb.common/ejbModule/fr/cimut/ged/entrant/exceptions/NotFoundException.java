package fr.cimut.ged.entrant.exceptions;

import org.apache.commons.httpclient.HttpStatus;

public class NotFoundException extends GedeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5004380087875301317L;

	public NotFoundException(String message) {
		super(message);
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.SC_NOT_FOUND;
	}
}
