package exceptions;

public class IncorrectClassRuntimeException extends RuntimeException{
    public IncorrectClassRuntimeException(){
        super("Incorrect class type");
    }
}
