package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import model.SessionData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.HttpFailureException;
import ui.ServerFacade;
import util.SerializerFactory;
import java.io.IOException;
import java.util.ArrayList;
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
        Assertions.assertEquals(username, actual.userData().getUsername());
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
        Assertions.assertEquals(username, actual.userData().getUsername());
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

        //assert that the auth token is no longer valid
        try {
            classUnderTest.getAllGames(session.authToken());
        } catch(HttpFailureException actualException) {
            Assertions.assertEquals(401, actualException.getStatusCode());
        }
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

        ArrayList<GameData> allGames = classUnderTest.getAllGames(session.authToken());
        Assertions.assertEquals(1, allGames.stream().filter(g -> g.gameName().equals(gameName)).count());
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

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void getAllGamesReturnsGameWithBoard() throws IOException, InterruptedException, HttpFailureException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        SessionData session = classUnderTest.registerUser(username, plainTextPassword, "zark@start.com");
        String authToken = session.authToken();
        String gameName1 = UUID.randomUUID().toString();
        classUnderTest.createGame(authToken, gameName1);

        ArrayList<GameData> actualList = classUnderTest.getAllGames(authToken);

        GameData actualGame = actualList.stream().filter(g -> g.gameName().equals(gameName1)).findFirst().get();
        ChessBoard actualBoard = actualGame.getGame().getBoard();
        System.out.println(actualBoard.toString());
        Assertions.assertEquals(32, actualBoard.toSquares().size());
    }

    @Test
    public void getAllGamesReturnsAllGamesIfUserLoggedIn() throws IOException, InterruptedException, HttpFailureException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        SessionData session = classUnderTest.registerUser(username, plainTextPassword, "zark@start.com");
        String authToken = session.authToken();
        String gameName1 = UUID.randomUUID().toString();
        String gameName2 = UUID.randomUUID().toString();
        String gameName3 = UUID.randomUUID().toString();
        classUnderTest.createGame(authToken, gameName1);
        classUnderTest.createGame(authToken, gameName2);
        classUnderTest.createGame(authToken, gameName3);

        ArrayList<GameData> actual = classUnderTest.getAllGames(authToken);

        Assertions.assertTrue(actual.size() >= 3);
        Assertions.assertEquals(1, actual.stream().filter(p -> p.gameName().equals(gameName1)).count());
        Assertions.assertEquals(1, actual.stream().filter(p -> p.gameName().equals(gameName2)).count());
        Assertions.assertEquals(1, actual.stream().filter(p -> p.gameName().equals(gameName3)).count());
    }

    @Test
    public void getAllGamesThrowsExceptionIfUserNotLoggedIn() throws IOException, InterruptedException
    {
        try {
            ArrayList<GameData> actual = classUnderTest.getAllGames(UUID.randomUUID().toString());
        } catch(HttpFailureException actualException) {
            Assertions.assertEquals(401, actualException.getStatusCode());
        }
    }

    @Test
    public void joinGameThrowsExceptionIfInputsInvalid() throws IOException, InterruptedException
    {
        SessionData session = new SessionData("", new UserData("", "", ""));

        try {
            classUnderTest.joinGame(0, session, ChessGame.TeamColor.WHITE);
            Assertions.fail("Should have thrown exception");
        } catch(HttpFailureException actualException) {
            Assertions.assertEquals(400, actualException.getStatusCode());
        }
    }

    @Test
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void joinGameWorks() throws IOException, InterruptedException, HttpFailureException
    {
        String username = UUID.randomUUID().toString();
        String plainTextPassword = UUID.randomUUID().toString();
        SessionData session = classUnderTest.registerUser(username, plainTextPassword, "zark@start.com");
        GameData gameData = classUnderTest.createGame(session.authToken(), UUID.randomUUID().toString());

        classUnderTest.joinGame(gameData.gameID(), session, ChessGame.TeamColor.BLACK);

        ArrayList<GameData> allGames = classUnderTest.getAllGames(session.authToken());
        GameData actualGame = allGames.stream().filter(g -> g.gameID() == gameData.gameID()).findFirst().get();
        Assertions.assertEquals(username, actualGame.blackUsername());
    }
}
