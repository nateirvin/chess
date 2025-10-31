package dataaccess;

import model.UpsertUserResult;
import model.UserData;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class UserMySqlProviderTests
{
    private UsersMySqlProvider classUnderTest;

    @BeforeEach
    public void setup() throws DataAccessException
    {
        this.classUnderTest = new UsersMySqlProvider();

        DatabaseManager.execute("DELETE FROM games");
        DatabaseManager.execute("DELETE FROM sessions");
        DatabaseManager.execute("DELETE FROM users");
    }

    @Test
    public void findOrCreateUserInsertsUserIfNoneExists() throws DataAccessException, SQLException
    {
        String username = "zork";
        String plainTextPassword = "string_bad";
        String email = "zap@branigan.net";
        UserData userData = new UserData(username, plainTextPassword, email);

        UpsertUserResult actual = classUnderTest.findOrCreateUser(userData);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(username, actual.username());
        Assertions.assertEquals(plainTextPassword, actual.password());
        Assertions.assertEquals(email, actual.email());
        Assertions.assertTrue(actual.isNew());

        int userCount = 0;

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, hashed_password, email FROM users";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        userCount++;

                        String actualUserName = rs.getString("username");
                        String actualPassword = rs.getString("hashed_password");
                        String actualEmail = rs.getString("email");

                        Assertions.assertEquals(username, actualUserName);
                        Assertions.assertNotEquals(plainTextPassword, actualPassword);
                        Assertions.assertEquals(email, actualEmail);
                    }
                }
            }
        }

        Assertions.assertEquals(1, userCount);
    }

    @Test
    public void findOrCreateUserReturnsExistingUserInfoIfUserAlreadyExists() throws DataAccessException, SQLException
    {
        String userName = "bosephus";
        String plainTextPassword = "i'm a plaintext password";
        int userId = insertTestUser(userName, plainTextPassword);
        UserData userData = new UserData(userName, plainTextPassword, null);

        UpsertUserResult actual = classUnderTest.findOrCreateUser(userData);

        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertFalse(actual.isNew());
        Assertions.assertEquals(userName, actual.username());
        Assertions.assertEquals(plainTextPassword, actual.password());
        Assertions.assertTrue(actual.email().isEmpty());
    }

    @Test
    public void getUserReturnsNullIfUserDoesNotExist()
    {
        UserData actual = classUnderTest.getUser(UUID.randomUUID().toString(), "any dream will do");

        Assertions.assertNull(actual);
    }

    @Test
    public void getUserReturnsUserIfUserExists() throws SQLException, DataAccessException
    {
        String userName = "samwise_the_fourth";
        String plainTextPassword = "i'm a plaintext password";
        int userId = insertTestUser(userName, plainTextPassword);

        UserData actual = classUnderTest.getUser(userName, plainTextPassword);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertEquals(userName, actual.username());
        Assertions.assertEquals(plainTextPassword, actual.password());
    }

    @Test
    public void deleteAllUsersRemovesAllUsers() throws DataAccessException, SQLException
    {
        insertTestUser(UUID.randomUUID().toString(), "joseph sees you");
        insertTestUser(UUID.randomUUID().toString(), "joseph sees you");
        insertTestUser(UUID.randomUUID().toString(), "joseph sees you");
        assert getUserCount() == 3;

        classUnderTest.deleteAllUsers();

        Assertions.assertEquals(0, getUserCount());
    }

    private static int insertTestUser(String userName, String plainTextPassword) throws SQLException, DataAccessException
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

    private static int getUserCount() throws SQLException, DataAccessException {
        int userCount = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        userCount++;
                    }
                }
            }
        }
        return userCount;
    }
}
