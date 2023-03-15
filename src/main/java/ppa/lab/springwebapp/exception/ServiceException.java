package ppa.lab.springwebapp.exception;

public class ServiceException extends Exception {
    private final String errorCode;
    private static final String DEFAULT_ERROR_CODE = "-1";

    public ServiceException(final String message, final String errorCode, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ServiceException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(final String message) {
        super(message);
        this.errorCode = DEFAULT_ERROR_CODE;
    }

    public ServiceException(final Throwable cause) {
        this(null, cause);
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = DEFAULT_ERROR_CODE;
    }

    public String getErrorCode() {
        return errorCode;
    }

}