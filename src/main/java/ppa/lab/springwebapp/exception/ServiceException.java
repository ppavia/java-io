package ppa.lab.springwebapp.exception;

public class ServiceException extends Exception {
    private String errorCode;

    public ServiceException(final String message, final String errorCode, final Throwable cause) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServiceException(final String message) {
        super(message);
    }

    public ServiceException(final Throwable cause) {
        this(null, cause);
    }

    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
