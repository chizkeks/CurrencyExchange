package exceptions;

public class NoSuchCurrencyException extends Exception {
    public NoSuchCurrencyException() {}
    public NoSuchCurrencyException(String message) {
        super(message);
    }
    public NoSuchCurrencyException(Throwable exception) {
        super(exception);
    }
}
