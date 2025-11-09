package ui;

public class AppState
{
    private String authToken;
    private String username;

    public void setSession(String authToken, String username) {
        if(authToken == null || authToken.isEmpty()) {
            throw new IllegalArgumentException("Cannot register blank auth token");
        }

        this.authToken = authToken;
        this.username = username;
    }

    public String currentUsername() {
        if(userIsLoggedIn()) {
            return username;
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
        username = null;
    }
}
