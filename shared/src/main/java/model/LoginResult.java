package model;

public record LoginResult(String username, String authToken) {
    public LoginResult(AuthData authData) {
        this(authData.username(), authData.token());
    }
}
