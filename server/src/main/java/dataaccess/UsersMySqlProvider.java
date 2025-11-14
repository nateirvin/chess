package dataaccess;

import model.UpsertUserResult;
import model.UserData;
import org.jetbrains.annotations.Nullable;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class UsersMySqlProvider implements UsersDataAccess
{
    public static void createTables() throws DataAccessException {
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
        String hashedPassword = Hasher.hash(userData.getPassword());

        String commandSql = "INSERT IGNORE INTO users (username, hashed_password, email) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement command = conn.prepareStatement(commandSql, RETURN_GENERATED_KEYS))
            {
                command.setString(1, userData.getUsername());
                command.setString(2, hashedPassword);
                command.setString(3, userData.getEmail() != null ? userData.getEmail() : "");

                command.executeUpdate();

                Integer userId = DatabaseManager.getIdentity(command);
                if (userId != null) {
                    userData.setId(userId);
                    return new UpsertUserResult(userData, true);
                } else {
                    userData = getUser(userData.getUsername());
                    userData.setPassword(null);
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
        assert challengePassword != null;
        return getUserInternal(username, challengePassword);
    }

    private UserData getUser(String username)
    {
        return getUserInternal(username, null);
    }

    @Nullable
    private static UserData getUserInternal(String username, String challengePassword) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var querySql =
                """
                SELECT user_id, username, hashed_password, email
                FROM users
                WHERE username = ?
                """;
            try (PreparedStatement command = conn.prepareStatement(querySql)) {
                command.setString(1, username);
                try (ResultSet rs = command.executeQuery()) {
                    if (rs.next()) {
                        UserData userData = readUserData(rs);
                        if(challengePassword != null) {
                            if(BCrypt.checkpw(challengePassword, userData.getPassword())) {
                                userData.setPassword(challengePassword);
                                return userData;
                            } else {
                                return null;
                            }
                        } else {
                            return userData;
                        }
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private static UserData readUserData(ResultSet resultSet) throws SQLException
    {
        int userId = resultSet.getInt(1);
        String username = resultSet.getString(2);
        String hashedPassword = resultSet.getString(3);
        String email = resultSet.getString(4);

        UserData userData = new UserData(username, hashedPassword, email);
        userData.setId(userId);

        return userData;
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
