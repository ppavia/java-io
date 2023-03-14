package fr.cimut.ged.entrant.exceptions;

import org.apache.commons.httpclient.HttpStatus;

public class UnauthorizedException extends GedeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4381668637944338757L;

	public UnauthorizedException(String message) {
		super(message);
	}

	@Override
	public int getHttpErrorCode() {
		return HttpStatus.SC_UNAUTHORIZED;
	}

}
