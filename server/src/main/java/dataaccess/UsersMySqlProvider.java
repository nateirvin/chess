package dataaccess;

import model.UpsertUserResult;
import model.UserData;

public class UsersMySqlProvider implements UsersDataAccess
{
    public UsersMySqlProvider() throws DataAccessException
    {
        DatabaseManager.createDatabase();

        String statement =
                """
                CREATE TABLE IF NOT EXISTS `users` (
                  `user_id` int unsigned NOT NULL AUTO_INCREMENT,
                  `username` varchar(50) NOT NULL,
                  `hashed_password` varchar(1000) NOT NULL,
                  `email` varchar(255) NOT NULL DEFAULT '',
                  PRIMARY KEY (`user_id`),
                  UNIQUE KEY `users_unique` (`username`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                """;

        DatabaseManager.executeSetup(statement, "failed to create table 'users'");
    }

    @Override
    public UpsertUserResult findOrCreateUser(UserData userData) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public UserData getUser(String username, String password) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAllUsers() {
        throw new RuntimeException("not implemented");
    }
}
