package service;

import dataaccess.SessionMemoryProvider;
import dataaccess.UsersMemoryProvider;
import model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.security.auth.login.LoginException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests
{
    private UserService classUnderTest;
    private UsersMemoryProvider usersDataAccess;

    @BeforeEach
    public void setup()
    {
        usersDataAccess = new UsersMemoryProvider();
        classUnderTest = new UserService(new SessionService(new SessionMemoryProvider()), usersDataAccess);
    }

    @Test
    public void registerCreatesUserIfUsernameNotToken()
    {
        assert usersDataAccess.getAllUsers().isEmpty();

        LoginResult actual = classUnderTest.register(
                new RegisterRequest("JohnBoy3", "aaa", "whatever@net.com"));

        assertNotNull(actual);
        assertEquals("JohnBoy3", actual.username());
    }

    @Test
    public void registerThrowsExceptionIfUsernameAlreadyTaken()
    {
        UserData userData = new UserData("JohnBoy2", "akldjfklasdjfkds", "none@none.net");
        usersDataAccess.findOrCreateUser(userData);

        try
        {
            classUnderTest.register(new RegisterRequest(userData.username(), userData.password(), userData.email()));
            fail("an exception should have been thrown");
        }
        catch(AlreadyTakenException actualException)
        {
            assertEquals("The username 'JohnBoy2' is already in use.", actualException.getMessage());
        }
    }

    @Test
    public void loginReturnsUserDataIfUsernameAndPasswordCorrect() throws LoginException
    {
        UserData userData = new UserData("JohnBoy4", "zamboni", "none@none.net");
        usersDataAccess.findOrCreateUser(userData);

        AuthData actual = classUnderTest.login(new LoginRequest("JohnBoy4", "zamboni"));

        assertNotNull(actual);
        assertEquals("JohnBoy4", actual.username());
    }

    @ParameterizedTest
    @ValueSource(strings = {"hi","there"})
    public void loginThrowsIfUsernameAndPasswordNotCorrect(String password)
    {
        UserData userData = new UserData("JohnBoy5", "zamboni", "none@none.net");
        usersDataAccess.findOrCreateUser(userData);

        try
        {
            classUnderTest.login(new LoginRequest("JohnBoy5", password));
            fail("should have thrown an exception");
        }
        catch (LoginException actualException)
        {
            assertEquals("unauthorized", actualException.getMessage());
        }
    }

    @Test
    public void resetDoesNothingIfNoUsersExist()
    {
        usersDataAccess.deleteAllUsers();
        assert usersDataAccess.getAllUsers().isEmpty();

        classUnderTest.reset();

        Collection<UserData> actual = usersDataAccess.getAllUsers();
        assertTrue(actual.isEmpty());
    }

    @Test
    public void resetClearsAllUsersIfTheyExist()
    {
        usersDataAccess.findOrCreateUser(
                new UserData("JohnBoy", "akldjfklasdjfkds", "none@none.net"));

        classUnderTest.reset();

        Collection<UserData> actual = usersDataAccess.getAllUsers();
        assertTrue(actual.isEmpty());
    }
}
