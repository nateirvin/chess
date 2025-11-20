package ui.data;

import model.AuthData;

public class AppState
{
    public static final String GUEST_USERNAME = "guest";

    private AuthData userSession;

    public void setSession(AuthData userSession) {
        if(userSession == null) {
            throw new IllegalArgumentException();
        }
        if(userIsLoggedIn()) {
            throw new IllegalStateException("You must end the previous session before assigning a new one.");
        }
        this.userSession = userSession;
    }

    public String currentUsername()
    {
        return userIsLoggedIn()
                ? userSession.username()
                : GUEST_USERNAME;
    }

    public boolean userIsLoggedIn() {
        return userSession != null;
    }

    public String getAuthToken() {
        return userSession.authToken();
    }

    public AuthData getSession() {
        return userSession;
    }

    public void endSession() {
        userSession = null;
    }
}
