package fr.cimut.ged.entrant.exceptions;

public class RuntimeGedeException extends RuntimeException {
	public RuntimeGedeException() {
	}

	public RuntimeGedeException(String message) {
		super(message);
	}

	public RuntimeGedeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuntimeGedeException(Throwable cause) {
		super(cause);
	}
}
