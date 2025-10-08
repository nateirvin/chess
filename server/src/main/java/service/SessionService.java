package service;

import dataaccess.SessionDataAccess;
import model.AuthData;

import java.util.UUID;

public class SessionService
{
    private final SessionDataAccess dataAccess;

    public SessionService(SessionDataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public AuthData createSession(String username)
    {
        return dataAccess.insertSession(UUID.randomUUID(), username);
    }
}
