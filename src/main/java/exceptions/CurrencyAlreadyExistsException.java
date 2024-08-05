package exceptions;

public class CurrencyAlreadyExistsException extends Exception {
    public CurrencyAlreadyExistsException() {}
    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
    public CurrencyAlreadyExistsException(Throwable exception) {
        super(exception);
    }
}
