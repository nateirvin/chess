package dataaccess;

import model.RegisterRequest;
import model.UpsertUserResult;
import model.UserData;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.UUID;

public class UserMySqlProviderTests
{
    private UsersMySqlProvider classUnderTest;

    @BeforeEach
    public void setup() throws DataAccessException
    {
        this.classUnderTest = new UsersMySqlProvider();
        TestHelper.ensureDatabaseSetup();
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
        Assertions.assertEquals(username, actual.getUsername());
        Assertions.assertEquals(plainTextPassword, actual.getPassword());
        Assertions.assertEquals(email, actual.getEmail());
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
        int userId = TestHelper.insertTestUser(userName, plainTextPassword);
        UserData userData = new UserData(userName, plainTextPassword, null);

        UpsertUserResult actual = classUnderTest.findOrCreateUser(userData);

        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertFalse(actual.isNew());
        Assertions.assertEquals(userName, actual.getUsername());
        Assertions.assertTrue(actual.getPassword() == null || actual.getPassword().isEmpty());
        Assertions.assertTrue(actual.getEmail().isEmpty());
    }

    @Test
    public void findOrCreateUserReturnsExistingUserInfoIfUserAlreadyExistsForWrongPassword() throws DataAccessException, SQLException
    {
        String userName = "bosephus";
        String correctPlainTextPassword = "i'm a plaintext password";
        int userId = TestHelper.insertTestUser(userName, correctPlainTextPassword);
        String incorrectPlainTextPassword = "bluey";
        UserData userData = new UserData(userName, incorrectPlainTextPassword, null);

        UpsertUserResult actual = classUnderTest.findOrCreateUser(userData);

        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertFalse(actual.isNew());
        Assertions.assertEquals(userName, actual.getUsername());
        Assertions.assertTrue(actual.getPassword() == null || actual.getPassword().isEmpty());
        Assertions.assertTrue(actual.getEmail().isEmpty());
    }

    @Test
    public void findOrCreateUserReturnsExistingUserInfoIfUserAlreadyExistsForRegisterOverload() throws DataAccessException, SQLException
    {
        String userName = "bosephus";
        String plainTextPassword = "i'm a plaintext password";
        int userId = TestHelper.insertTestUser(userName, plainTextPassword);
        UserData userData = new UserData(new RegisterRequest(userName, plainTextPassword, null));

        UpsertUserResult actual = classUnderTest.findOrCreateUser(userData);

        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertFalse(actual.isNew());
        Assertions.assertEquals(userName, actual.getUsername());
        Assertions.assertTrue(actual.getPassword() == null || actual.getPassword().isEmpty());
        Assertions.assertTrue(actual.getEmail().isEmpty());
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
        int userId = TestHelper.insertTestUser(userName, plainTextPassword);

        UserData actual = classUnderTest.getUser(userName, plainTextPassword);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertEquals(userName, actual.getUsername());
        Assertions.assertEquals(plainTextPassword, actual.getPassword());
    }

    @Test
    public void deleteAllUsersRemovesAllUsers() throws DataAccessException, SQLException
    {
        TestHelper.insertTestUser(UUID.randomUUID().toString(), "joseph sees you");
        TestHelper.insertTestUser(UUID.randomUUID().toString(), "joseph sees you");
        TestHelper.insertTestUser(UUID.randomUUID().toString(), "joseph sees you");
        assert getUserCount() == 3;

        classUnderTest.deleteAllUsers();

        Assertions.assertEquals(0, getUserCount());
    }

    private static int getUserCount() throws SQLException, DataAccessException {
        return TestHelper.getRowCountForTable("users");
    }
}
