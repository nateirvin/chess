package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.*;
import service.AlreadyTakenException;
import java.sql.*;
import java.util.UUID;

public class SessionMySqlProviderTests
{
    private SessionMySqlProvider classUnderTest;

    @BeforeEach
    public void setup() throws DataAccessException
    {
        this.classUnderTest = new SessionMySqlProvider();
        TestHelper.resetDatabase();
    }

    @Test
    public void getSessionReturnsNullIfNoSessionExists()
    {
        AuthData actual = classUnderTest.getSession(UUID.randomUUID().toString());

        Assertions.assertNull(actual);
    }

    @Test
    public void insertSessionCreatesSession() throws SQLException, DataAccessException
    {
        String authToken = UUID.randomUUID().toString();
        int userId = TestHelper.insertTestUser("greg_boy", "what's in a name");

        AuthData actual = classUnderTest.insertSession(authToken, "greg_boy");

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement =
                    """
                    SELECT user_id
                    FROM sessions
                    WHERE auth_token = ?
                    """;
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int actualUserId = rs.getInt(1);
                        Assertions.assertEquals(userId, actualUserId);
                    }
                }
            }
        }
        Assertions.assertEquals(authToken, actual.authToken());
        Assertions.assertEquals("greg_boy", actual.username());
    }

    @Test
    public void getSessionReturnsSessionIfSessionExists() throws SQLException, DataAccessException
    {
        String authToken = UUID.randomUUID().toString();
        TestHelper.insertTestUser("zupa");
        classUnderTest.insertSession(authToken, "zupa");

        AuthData actual = classUnderTest.getSession(authToken);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(authToken, actual.authToken());
        Assertions.assertEquals("zupa", actual.username());
    }

    @Test
    public void insertSessionThrowsIfSessionAlreadyExists()
    {
        String authToken = UUID.randomUUID().toString();
        try
        {
            TestHelper.insertTestUser("hambone");
            TestHelper.insertTestUser("manoflamancha");
        } catch(Exception e)
        {
            Assertions.fail(e.getMessage());
        }
        classUnderTest.insertSession(authToken, "manoflamancha");

        try
        {
            classUnderTest.insertSession(authToken, "hambone");
            Assertions.fail("should have thrown an exception");
        }
        catch(AlreadyTakenException actualException)
        {
            Assertions.assertEquals("The auth token '"+authToken+"' is already in use.",
                    actualException.getMessage());
        }
    }

    @Test
    public void deleteSessionRemovesSessionIfSessionExists() throws SQLException, DataAccessException
    {
        String authToken = UUID.randomUUID().toString();
        String username = "russell_elliot";
        TestHelper.insertTestUser(username);
        classUnderTest.insertSession(authToken, username);

        classUnderTest.deleteSession(authToken);

        AuthData actual = classUnderTest.getSession(authToken);
        Assertions.assertNull(actual);
    }

    @Test
    public void deleteSessionDoesNothingIfSessionDoesNotExist()
    {
        String authToken = UUID.randomUUID().toString();

        classUnderTest.deleteSession(authToken);
    }

    @Test
    public void deleteAllSessionsDeletesAllSession() throws SQLException, DataAccessException
    {
        TestHelper.insertTestUser("georgie_porgie");
        classUnderTest.insertSession(UUID.randomUUID().toString(), "georgie_porgie");
        classUnderTest.insertSession(UUID.randomUUID().toString(), "georgie_porgie");
        classUnderTest.insertSession(UUID.randomUUID().toString(), "georgie_porgie");

        classUnderTest.deleteAllSessions();

        Assertions.assertEquals(0, TestHelper.getRowCountForTable("sessions"));
    }
}
