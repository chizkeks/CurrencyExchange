package exceptions;

public class CurrencyPairAlreadyExistsException extends Exception {
    public CurrencyPairAlreadyExistsException() {
    }

    public CurrencyPairAlreadyExistsException(String message) {
        super(message);
    }

    public CurrencyPairAlreadyExistsException(Throwable exception) {
        super(exception);
    }
}