package dataaccess;

import model.AuthData;
import service.AlreadyTakenException;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SessionMySqlProvider implements SessionDataAccess
{
    public SessionMySqlProvider()  throws DataAccessException
    {
        DatabaseManager.createDatabase();
        UsersMySqlProvider.createTables();
        createTables();
    }

    protected static void createTables() throws DataAccessException {
        String statement =
                """     
                CREATE TABLE IF NOT EXISTS `sessions` (
                  `session_id` int unsigned NOT NULL AUTO_INCREMENT,
                  `auth_token` varchar(100) NOT NULL,
                  `user_id` int unsigned NOT NULL,
                  PRIMARY KEY (`session_id`),
                  UNIQUE KEY `sessions_unique` (`auth_token`),
                  KEY `sessions_users_FK` (`user_id`),
                  CONSTRAINT `sessions_users_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                """;

        DatabaseManager.execute(statement, "failed to create table 'sessions'");
    }

    @Override
    public AuthData insertSession(String authToken, String username)
    {
        String commandSql = """
            INSERT INTO sessions (auth_token, user_id)
            VALUES (?, (SELECT user_id FROM users WHERE username = ?))
            """;
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement command = conn.prepareStatement(commandSql, RETURN_GENERATED_KEYS))
            {
                command.setString(1, authToken);
                command.setString(2, username);

                command.executeUpdate();

                return new AuthData(authToken, username);
            }
        }
        catch(SQLIntegrityConstraintViolationException uniqueException)
        {
            throw new AlreadyTakenException("auth token", authToken);
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getSession(String authToken)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            var querySql =
                    """
                    SELECT users.username
                    FROM sessions
                        INNER JOIN users
                            ON sessions.user_id = users.user_id
                    WHERE sessions.auth_token = ?
                    """;
            try (PreparedStatement command = conn.prepareStatement(querySql))
            {
                command.setString(1, authToken);

                try (ResultSet rs = command.executeQuery())
                {
                    if (rs.next())
                    {
                        return new AuthData(authToken, rs.getString(1));
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
    public void deleteSession(String authToken)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement command = conn.prepareStatement("DELETE FROM sessions WHERE auth_token = ?"))
            {
                command.setString(1, authToken);
                command.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllSessions()
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement command = conn.prepareStatement("DELETE FROM sessions"))
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
