package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;

public class SessionMemoryProvider implements SessionDataAccess
{
    private static final HashMap<String, AuthData> SESSIONS = new HashMap<>();

    @Override
    public AuthData insertSession(String authToken, String username)
    {
        AuthData authData = new AuthData(authToken, username);
        SESSIONS.put(authToken, authData);
        return authData;
    }

    @Override
    public AuthData getSession(String authToken)
    {
        return SESSIONS.getOrDefault(authToken, null);
    }

    @Override
    public void deleteSession(String authToken)
    {
        SESSIONS.remove(authToken);
    }

    @Override
    public void deleteAllSessions() {
        SESSIONS.clear();
    }

    public Collection<AuthData> getAllSessions() {
        return SESSIONS.values();
    }
}
