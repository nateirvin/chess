package ui.data;

public class HttpFailureException extends Exception {
    private final int statusCode;

    public HttpFailureException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpFailureException(int statusCode, Exception baseException) {
        super("HTTP fault", baseException);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
