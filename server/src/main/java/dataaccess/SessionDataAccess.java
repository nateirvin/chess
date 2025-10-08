package dataaccess;

import model.AuthData;

import java.util.UUID;

public interface SessionDataAccess
{
    AuthData insertSession(UUID id, String username);
}
