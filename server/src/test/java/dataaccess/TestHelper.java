package dataaccess;

import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class TestHelper
{
    public static void ensureDatabaseSetup() throws DataAccessException
    {
        DatabaseManager.createDatabase();
        UsersMySqlProvider.createTables();
        SessionMySqlProvider.createTables();
        GameMySqlProvider.createTables();

        DatabaseManager.execute("DELETE FROM games");
        DatabaseManager.execute("DELETE FROM sessions");
        DatabaseManager.execute("DELETE FROM users");
    }

    public static int insertTestUser(String userName) throws SQLException, DataAccessException {
        return insertTestUser(userName, UUID.randomUUID().toString());
    }

    static int insertTestUser(String userName, String plainTextPassword) throws SQLException, DataAccessException
    {
        int originalId;
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, hashed_password) VALUES (?, ?)", RETURN_GENERATED_KEYS))
            {
                ps.setString(1, userName);
                ps.setString(2, Hasher.hash(plainTextPassword));

                ps.executeUpdate();

                originalId = DatabaseManager.getIdentity(ps);
            }
        }
        assert originalId != 0;
        return originalId;
    }

    static int getRowCountForTable(String tableName) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(1) FROM " + tableName)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return 0;
    }
}
