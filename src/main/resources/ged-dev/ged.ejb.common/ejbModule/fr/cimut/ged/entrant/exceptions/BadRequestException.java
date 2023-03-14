package fr.cimut.ged.entrant.exceptions;

import org.apache.commons.httpclient.HttpStatus;

public class BadRequestException extends GedeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5004380087875301317L;

	public BadRequestException(String message) {
		this(message, HttpStatus.SC_BAD_REQUEST);
	}

	public BadRequestException(String message, int httpErrorCode) {
		super(message);
		setHttpErrorCode(httpErrorCode);
	}

}
