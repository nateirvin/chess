package dataaccess;

import model.UpsertUserResult;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

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

        DatabaseManager.execute(statement, "failed to create table 'users'");
    }

    @Override
    public UpsertUserResult findOrCreateUser(UserData userData)
    {
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());

        String statement = "INSERT INTO users (username, hashed_password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS))
            {
                ps.setString(1, userData.username());
                ps.setString(2, hashedPassword);
                ps.setString(3, userData.email());

                ps.executeUpdate();

                return new UpsertUserResult(userData, true);
            }
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }
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
