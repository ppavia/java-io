package ppa.lab.springwebapp.exception;

public class DtoException extends Exception {
    private final String errorCode;
    private static final String DEFAULT_ERROR_CODE = "-1";

    public DtoException(final String message, final String errorCode, final Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public DtoException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DtoException(final String message) {
        super(message);
        this.errorCode = DEFAULT_ERROR_CODE;
    }

    public DtoException(final Throwable cause) {
        this(null, cause);
    }

    public DtoException(final String message, final Throwable cause) {
        super(message, cause);
        this.errorCode = DEFAULT_ERROR_CODE;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
