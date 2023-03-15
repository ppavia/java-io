package ppa.lab.springwebapp.exception;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {
    private final HttpStatus httpStatus;

    public RestException(String message, HttpStatus httpStatus, final Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public RestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
