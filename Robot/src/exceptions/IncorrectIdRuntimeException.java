package exceptions;

public class IncorrectIdRuntimeException extends RuntimeException{

    public IncorrectIdRuntimeException() {
        super("Incorrect id");
    }

    public IncorrectIdRuntimeException(String message) {
        super(message);
    }

    public IncorrectIdRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
