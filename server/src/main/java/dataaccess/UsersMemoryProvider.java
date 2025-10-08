package dataaccess;

import model.UpsertUserResult;
import model.UserData;

import java.util.HashMap;

public class UsersMemoryProvider implements UsersDataAccess {
    private static HashMap<String, UserData> users = new HashMap<>();

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
}
