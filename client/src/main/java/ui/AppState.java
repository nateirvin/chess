package ui;

import model.SessionData;
import model.UserData;

public class AppState
{
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

    public Integer getUserID() {
        if(userIsLoggedIn()) {
            return currentSession.userData().getId();
        }
        return null;
    }

    public String currentUsername()
    {
        return userIsLoggedIn()
                ? currentSession.userData().username()
                : RegisterUserCommand.GUEST_USERNAME;
    }

    public boolean userIsLoggedIn() {
        return currentSession != null;
    }

    public String getAuthToken() {
        return currentSession.authToken();
    }

    public void endSession() {
        currentSession = null;
    }
}
