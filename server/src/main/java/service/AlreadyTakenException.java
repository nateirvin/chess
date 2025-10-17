package service;

public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException(String entityKind, String entity) {
        super("The " + entityKind + " '" + entity + "' is already in use.");
    }
}
