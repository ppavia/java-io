package ppa.lab.springwebapp.exception;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {
    private HttpStatus httpStatus;

    public RestException(final String message) {
        super(message);
    }

    public RestException(final Throwable cause) {
        this(null, cause);
    }

    public RestException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
