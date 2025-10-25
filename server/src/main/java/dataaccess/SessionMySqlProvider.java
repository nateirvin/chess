package dataaccess;

import model.AuthData;

public class SessionMySqlProvider implements SessionDataAccess
{
    public SessionMySqlProvider()  throws DataAccessException
    {
        DatabaseManager.createDatabase();

        String statement =
                """     
                CREATE TABLE IF NOT EXISTS `sessions` (
                  `session_id` int unsigned NOT NULL AUTO_INCREMENT,
                  `auth_token` varchar(1000) NOT NULL,
                  `user_id` int unsigned NOT NULL,
                  PRIMARY KEY (`session_id`),
                  KEY `sessions_users_FK` (`user_id`),
                  CONSTRAINT `sessions_users_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                """;

        DatabaseManager.executeSetup(statement, "failed to create table 'sessions'");
    }

    @Override
    public AuthData insertSession(String authToken, String username) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public AuthData getSession(String authToken) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteSession(String authToken) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAllSessions() {
        throw new RuntimeException("not implemented");
    }
}
