package dataaccess;

import model.UpsertUserResult;
import model.UserData;

import java.util.HashMap;

public class UsersMemoryProvider implements UsersDataAccess
{
    private static final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public UpsertUserResult findOrCreateUser(UserData userData)
    {
        if(users.containsKey(userData.username()))
        {
            UserData data = users.get(userData.username());
            return new UpsertUserResult(data, false);
        }
        else
        {
            users.put(userData.username(), userData);
            return new UpsertUserResult(userData, true);
        }
    }

    @Override
    public UserData getUser(String username, String password)
    {
        if(users.containsKey(username)){
            UserData userData = users.get(username);
            if(userData.password().equals(password)) {
                return userData;
            }
        }

        return null;
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}
