package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class SessionMemoryProvider implements SessionDataAccess
{
    private static final HashMap<UUID, String> sessions = new HashMap<>();

    @Override
    public AuthData insertSession(UUID id, String username)
    {
        sessions.put(id, username);
        return new AuthData(id.toString(), username);
    }
}
