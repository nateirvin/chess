package service;

import dataaccess.SessionMemoryProvider;
import model.AuthData;
import org.junit.jupiter.api.*;

import javax.security.auth.login.LoginException;

import static org.junit.jupiter.api.Assertions.*;

public class SessionServiceTests
{

    private SessionMemoryProvider dataAccess;
    private SessionService classUnderTest;

    @BeforeEach
    public void setup()
    {
        dataAccess = new SessionMemoryProvider();
        classUnderTest = new SessionService(dataAccess);
    }

    @Test
    public void createSessionCreatesSessionWithIdAndUserName()
    {
        AuthData actual = classUnderTest.createSession("Fred1");

        assertNotNull(actual);
        assertEquals("Fred1", actual.username());
        assertFalse(actual.authToken().isEmpty());
    }

    @Test
    public void createSessionReturnsDifferentIdEachTime()
    {
        AuthData actual1 = classUnderTest.createSession("alkssjdf");
        AuthData actual2 = classUnderTest.createSession("ZZZtop");
        AuthData actual3 = classUnderTest.createSession("ZZZtop");

        assertNotEquals(actual1.authToken(), actual2.authToken());
        assertNotEquals(actual3.authToken(), actual2.authToken());
    }

    @Test
    public void validateSessionThrowsExceptionIfAuthTokenMissing()
    {

        try {
            classUnderTest.validateSession("zzz");
            fail("should have thrown an exception");
        } catch(LoginException actualException) {
            assertEquals("unauthorized", actualException.getMessage());
        }
    }

    @Test
    public void validateSessionReturnsAuthDataIfSessionFound() throws LoginException
    {
        dataAccess.insertSession("def", "ryan");

        AuthData actual = classUnderTest.validateSession("def");

        assertNotNull(actual);
        assertEquals("ryan", actual.username());
        assertFalse(actual.authToken().isEmpty());
    }

    @Test
    public void closeSessionDeletesSessionIfSessionExists()
    {
        dataAccess.insertSession("def", "ryan");
        dataAccess.insertSession("bbb", "george");

        classUnderTest.closeSession("def");

        assertNull(dataAccess.getSession("def"));
        assertNotNull(dataAccess.getSession("bbb"));
    }

    @Test
    public void closeSessionDoesNothingIfSessionDoesNotExist()
    {
        dataAccess.insertSession("def", "ryan");
        int initialCount = dataAccess.getAllSessions().size();
        assert initialCount == 1;
        assert dataAccess.getSession("abc") == null;

        classUnderTest.closeSession("abc");

        assertEquals(1, dataAccess.getAllSessions().size());
        assertNotNull(dataAccess.getSession("def"));
    }

    @Test
    public void resetDeletesAllSessionsIfSessionsExist()
    {
        dataAccess.insertSession("aksljdf","asdlkfjasdf");
        dataAccess.insertSession("aksl22jdf","asdlkfjasdf");
        dataAccess.insertSession("12222","asdlkfjasdf");
        assert !dataAccess.getAllSessions().isEmpty();

        classUnderTest.reset();

        assertTrue(dataAccess.getAllSessions().isEmpty());
    }

    @Test
    public void resetDoesNothingIfNoSessionsExist()
    {
        dataAccess.deleteAllSessions();
        assert dataAccess.getAllSessions().isEmpty();

        classUnderTest.reset();

        assertTrue(dataAccess.getAllSessions().isEmpty());
    }
}
