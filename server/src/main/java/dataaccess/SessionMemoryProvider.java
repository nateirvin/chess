package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class SessionMemoryProvider implements SessionDataAccess
{
    private static final HashMap<String, AuthData> sessions = new HashMap<>();

    @Override
    public AuthData insertSession(String authToken, String username)
    {
        AuthData authData = new AuthData(authToken, username);
        sessions.put(authToken, authData);
        return authData;
    }

    @Override
    public AuthData getSession(String authToken)
    {
        return sessions.containsKey(authToken) ? sessions.get(authToken) : null;
    }

    @Override
    public void deleteSession(String authToken)
    {
        sessions.remove(authToken);
    }

    @Override
    public void deleteAllSessions() {
        sessions.clear();
    }
}
