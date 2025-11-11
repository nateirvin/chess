package model;

public class UserEntryResult<T> {
    private T value;
    private String errorMessage;

    public UserEntryResult(T value) {
        this.value = value;
    }

    public UserEntryResult(String errorMessage) {
        if(errorMessage == null) {
            throw new IllegalArgumentException();
        }
        this.errorMessage = errorMessage.trim();
    }

    public boolean success() {
        return errorMessage == null;
    }

    public T getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
