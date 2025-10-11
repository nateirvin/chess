package service;

import dataaccess.SessionDataAccess;
import model.AuthData;

import javax.security.auth.login.LoginException;
import java.util.UUID;

@SuppressWarnings("ClassCanBeRecord")
public class SessionService
{
    private final SessionDataAccess dataAccess;

    public SessionService(SessionDataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public AuthData createSession(String username)
    {
        return dataAccess.insertSession(UUID.randomUUID().toString(), username);
    }

    public AuthData validateSession(String authToken) throws LoginException
    {
        if(authToken == null || authToken.isEmpty())
        {
            throw new IllegalArgumentException();
        }

        AuthData authData = dataAccess.getSession(authToken);

        if(authData == null)
        {
            throw new LoginException("unauthorized");
        }

        return authData;
    }

    public void closeSession(String authToken)
    {
        dataAccess.deleteSession(authToken);
    }

    public void reset() {
        dataAccess.deleteAllSessions();
    }
}
