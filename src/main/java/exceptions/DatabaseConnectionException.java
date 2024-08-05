package exceptions;

public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException() {}
    public DatabaseConnectionException(String message) {
        super(message);
    }
    public DatabaseConnectionException(Throwable exception) {
        super(exception);
    }
}
