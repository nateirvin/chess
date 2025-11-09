package ui;

import model.UserData;

public class AppState
{
    private String authToken;
    private UserData user;

    public void setSession(String authToken, UserData userData) {
        if(authToken == null || authToken.isEmpty()) {
            throw new IllegalArgumentException("Cannot register blank auth token");
        }

        this.authToken = authToken;
        this.user = userData;
    }

    public Integer getUserID() {
        if(userIsLoggedIn()) {
            return user.getId();
        }
        return null;
    }

    public String currentUsername() {
        if(userIsLoggedIn()) {
            return user.username();
        }
        return RegisterUserCommand.GUEST_USERNAME;
    }

    public boolean userIsLoggedIn() {
        return authToken != null;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void endSession() {
        authToken = null;
        user = null;
    }
}
