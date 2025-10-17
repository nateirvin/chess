package dataaccess;

import model.UpsertUserResult;
import model.UserData;

public interface UsersDataAccess
{
    UpsertUserResult findOrCreateUser(UserData userData);
    UserData getUser(String username, String password);
    void deleteAllUsers();
}
