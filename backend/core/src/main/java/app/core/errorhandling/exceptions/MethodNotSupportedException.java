package app.core.errorhandling.exceptions;

public class MethodNotSupportedException extends RuntimeException {
    public MethodNotSupportedException(String message) {
        super(message);
    }
}
