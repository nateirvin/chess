package dataaccess;

import model.UpsertUserResult;
import model.UserData;
import org.junit.jupiter.api.*;
import java.sql.*;

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
    public void findOrCreateUserInsertsUserIfNoneExists() throws DataAccessException
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
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

        Assertions.assertEquals(1, userCount);
    }

    @Test
    public void findOrCreateUserReturnsExistingUserInfoIfUserAlreadyExists() throws DataAccessException, SQLException
    {
        int originalId;
        String userName = "bosephus";
        String plainTextPassword = "i'm a plaintext password";
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
        UserData userData = new UserData(userName, plainTextPassword, null);

        UpsertUserResult actual = classUnderTest.findOrCreateUser(userData);

        Assertions.assertEquals(originalId, actual.getId());
        Assertions.assertFalse(actual.isNew());
        Assertions.assertEquals(userName, actual.username());
        Assertions.assertEquals(plainTextPassword, actual.password());
        Assertions.assertTrue(actual.email().isEmpty());
    }
}
