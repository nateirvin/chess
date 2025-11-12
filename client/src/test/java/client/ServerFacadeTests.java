package client;

import model.SessionData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.HttpFailureException;
import ui.ServerFacade;
import util.SerializerFactory;
import java.io.IOException;
import java.util.UUID;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade classUnderTest;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(1414);
        System.out.println("Started test HTTP server on " + port);

        classUnderTest = new ServerFacade(new SerializerFactory().getGson());
        classUnderTest.bindTo("localhost", port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerUserReturnsSessionData() throws IOException, InterruptedException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        String email = "zam@zam.net";

        SessionData actual = classUnderTest.registerUser(username, plainTextPassword, email);

        Assertions.assertFalse(actual.authToken().isEmpty());
        Assertions.assertNotEquals(0, actual.userData().getId());
        Assertions.assertEquals(username, actual.userData().username());
    }

    @Test
    public void registerUserIfUsernameAlreadyTaken() throws IOException, InterruptedException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        classUnderTest.registerUser(username, plainTextPassword, "");

        SessionData actual = classUnderTest.registerUser(username, plainTextPassword, "");

        Assertions.assertNull(actual);
    }

    @Test
    public void loginUserWorks() throws IOException, InterruptedException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        classUnderTest.registerUser(username, plainTextPassword, null);

        SessionData actual = classUnderTest.loginUser(username, plainTextPassword);

        Assertions.assertFalse(actual.authToken().isEmpty());
        Assertions.assertNotEquals(0, actual.userData().getId());
        Assertions.assertEquals(username, actual.userData().username());
    }

    @Test
    public void loginUserFails() throws IOException, InterruptedException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        classUnderTest.registerUser(username, plainTextPassword, null);

        SessionData actual = classUnderTest.loginUser(username, "incorrect");

        Assertions.assertNull(actual);
    }

    @Test
    public void logoutUserWorks() throws IOException, InterruptedException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        SessionData session = classUnderTest.registerUser(username, plainTextPassword, "zark@start.com");

        classUnderTest.logoutUser(session.authToken());
    }

    @Test
    public void logoutUserThrowsUseableExceptionIfAuthNotValid() throws IOException, InterruptedException
    {
        try {
            classUnderTest.logoutUser(UUID.randomUUID().toString());
            Assertions.fail("should have thrown an exception");
        } catch(RuntimeException actualException) {
            Assertions.assertEquals("Failed to logout", actualException.getMessage());
        }
    }

    @Test
    public void createGameWorks() throws IOException, InterruptedException, HttpFailureException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        SessionData session = classUnderTest.registerUser(username, plainTextPassword, "zark@start.com");
        String gameName = UUID.randomUUID().toString();

        classUnderTest.createGame(session.authToken(), gameName);
    }

    @Test
    public void createGameThrowsUnauthorizedIfUserNotLoggedIn() throws IOException, InterruptedException
    {
        String authToken = UUID.randomUUID().toString();
        String gameName = UUID.randomUUID().toString();

        try {
            classUnderTest.createGame(authToken, gameName);
            Assertions.fail("should have thrown exception");
        } catch(HttpFailureException actualException) {
            Assertions.assertEquals(401, actualException.getStatusCode());
        }
    }
}
