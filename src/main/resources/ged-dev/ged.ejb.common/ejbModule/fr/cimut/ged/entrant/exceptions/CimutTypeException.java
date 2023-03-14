package fr.cimut.ged.entrant.exceptions;

public class CimutTypeException extends GedeException {

	public CimutTypeException(String message) {
		super(message);
	}

	public CimutTypeException(Exception e) {
		super(e);
	}

	public CimutTypeException(String message, Throwable e) {
		super(message, e);
	}
}
