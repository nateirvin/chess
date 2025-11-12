package client;

import model.SessionData;
import org.junit.jupiter.api.*;
import server.Server;
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
}
