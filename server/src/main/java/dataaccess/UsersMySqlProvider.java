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
        createTables();
    }

    protected static void createTables() throws DataAccessException {
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
        String hashedPassword = Hasher.hash(userData.password());

        String commandSql = "INSERT IGNORE INTO users (username, hashed_password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement command = conn.prepareStatement(commandSql, RETURN_GENERATED_KEYS))
            {
                command.setString(1, userData.username());
                command.setString(2, hashedPassword);
                command.setString(3, userData.email() != null ? userData.email() : "");

                command.executeUpdate();

                Integer userId = DatabaseManager.getIdentity(command);
                if (userId != null) {
                    return new UpsertUserResult(userData, true);
                } else {
                    userData = getUser(userData.username(), userData.password());
                    return new UpsertUserResult(userData, false);
                }
            }
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username, String challengePassword)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            var querySql = "SELECT user_id, hashed_password, email FROM users WHERE username = ?";
            try (PreparedStatement command = conn.prepareStatement(querySql))
            {
                command.setString(1, username);

                try (ResultSet rs = command.executeQuery())
                {
                    if (rs.next())
                    {
                        String actualPassword = rs.getString(2);
                        if(BCrypt.checkpw(challengePassword, actualPassword))
                        {
                            var email = rs.getString(3);
                            UserData userData = new UserData(username, challengePassword, email);
                            userData.setId(rs.getInt(1));
                            return userData;
                        }
                    }
                }
            }
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void deleteAllUsers()
    {
        String commandSql = "DELETE FROM users";
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement command = conn.prepareStatement(commandSql))
            {
                command.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
