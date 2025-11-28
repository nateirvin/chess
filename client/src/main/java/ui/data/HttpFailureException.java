package ui.data;

public class HttpFailureException extends Exception {
    private final int statusCode;

    public HttpFailureException(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
