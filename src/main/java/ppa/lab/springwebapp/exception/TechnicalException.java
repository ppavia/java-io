package ppa.lab.springwebapp.exception;

public class TechnicalException extends Exception {

    private final String type;
    private static final String DEFAULT_TYPE_EXCEPTION = "RuntimeException";

    public TechnicalException(final String message, final String type, final Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public TechnicalException(final String message, final String type) {
        super(message);
        this.type = type;
    }

    public TechnicalException(final String message) {
        super(message);
        this.type = DEFAULT_TYPE_EXCEPTION;
    }

    public TechnicalException(final Throwable cause) {
        this(null, cause);
    }

    public TechnicalException(final String message, final Throwable cause) {
        super(message, cause);
        this.type = DEFAULT_TYPE_EXCEPTION;
    }
}
