package exceptions;

public class NoSuchExchangeRateException extends Exception{
    public NoSuchExchangeRateException() {}
    public NoSuchExchangeRateException(String message) {
        super(message);
    }
    public NoSuchExchangeRateException(Throwable exception) {
        super(exception);
    }
}
