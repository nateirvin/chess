package ui;

import model.SessionData;

public class AppState
{
    public static final String GUEST_USERNAME = "guest";

    private SessionData currentSession;

    public void setSession(SessionData sessionData) {
        if(sessionData == null) {
            throw new IllegalArgumentException();
        }
        if(userIsLoggedIn()) {
            throw new IllegalStateException("You must enter the previous session before assigning a new one.");
        }
        this.currentSession = sessionData;
    }

    public String currentUsername()
    {
        return userIsLoggedIn()
                ? currentSession.userData().getUsername()
                : GUEST_USERNAME;
    }

    public boolean userIsLoggedIn() {
        return currentSession != null;
    }

    public String getAuthToken() {
        return currentSession.authToken();
    }

    public SessionData getSession() {
        return currentSession;
    }

    public void endSession() {
        currentSession = null;
    }
}
