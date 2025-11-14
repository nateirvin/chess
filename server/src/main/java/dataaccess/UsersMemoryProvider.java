package dataaccess;

import model.UpsertUserResult;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;

public class UsersMemoryProvider implements UsersDataAccess
{
    private static final HashMap<String, UserData> USERS = new HashMap<>();

    @Override
    public UpsertUserResult findOrCreateUser(UserData userData)
    {
        if(USERS.containsKey(userData.getUsername()))
        {
            UserData data = USERS.get(userData.getUsername());
            return new UpsertUserResult(data, false);
        }
        else
        {
            USERS.put(userData.getUsername(), userData);
            return new UpsertUserResult(userData, true);
        }
    }

    @Override
    public UserData getUser(String username, String password)
    {
        if(USERS.containsKey(username)){
            UserData userData = USERS.get(username);
            if(userData.getPassword().equals(password)) {
                return userData;
            }
        }

        return null;
    }

    @Override
    public void deleteAllUsers() {
        USERS.clear();
    }

    public Collection<UserData> getAllUsers() {
        return USERS.values();
    }
}
