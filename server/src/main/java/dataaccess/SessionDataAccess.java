package dataaccess;

import model.AuthData;

public interface SessionDataAccess
{
    AuthData insertSession(String authToken, String username);
    AuthData getSession(String authToken);
    void deleteSession(String authToken);
    void deleteAllSessions();
}
