package exceptions;

public class IllegalOperation extends RuntimeException{
    public IllegalOperation(){
        super("This operation is illegal");
    }
}
