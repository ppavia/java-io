package fr.cimut.ged.entrant.exceptions;

import org.apache.commons.httpclient.HttpStatus;

public class GedeCommonException extends GedeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5004380087875301317L;

	public GedeCommonException(String message) {
		super(message);
	}

	public GedeCommonException(String message, Exception e) {
		super(message, e);
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.SC_INTERNAL_SERVER_ERROR;
	}

}
