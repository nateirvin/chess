package model;

public record LoginResult(String username, String authToken, int userId) {
    public LoginResult(AuthData authData) {
        this(authData.username(), authData.authToken(), authData.userId());
    }
}
