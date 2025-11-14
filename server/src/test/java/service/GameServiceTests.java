package service;

import chess.ChessGame;
import dataaccess.GameMemoryProvider;
import model.CreateGameRequest;
import model.GameData;
import model.UpsertGameResult;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests
{
    private GameMemoryProvider dataAccess;
    private GameService classUnderTest;

    @BeforeEach
    public void setup()
    {
        dataAccess = new GameMemoryProvider();
        classUnderTest = new GameService(dataAccess);
    }

    @Test
    public void createGameCreatesGame()
    {
        assert dataAccess.getAllGames().stream().noneMatch(g -> g.gameName().equals("fake_game"));

        GameData actual = classUnderTest.createGame(new CreateGameRequest("fake_game"));

        assertNotNull(actual);
        assertEquals("fake_game", actual.gameName());
        assertTrue(actual.gameID() != 0);
    }

    @Test
    public void createGameThrowsIfGameAlreadyExists()
    {
        dataAccess.findOrCreateGame("zork", new ChessGame());

        try{
            classUnderTest.createGame(new CreateGameRequest("zork"));
            fail("should have thrown an exception");
        } catch(AlreadyTakenException actualException) {
            assertEquals("The game 'zork' is already in use.", actualException.getMessage());
        }
    }

    @Test
    public void getGamesReturnsNothingIfNoGamesFound()
    {
        dataAccess.deleteAllGames();
        assert dataAccess.getAllGames().isEmpty();

        ArrayList<GameData> actual = classUnderTest.getGames();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void getGamesReturnsGamesIfSomeFound()
    {
        dataAccess.findOrCreateGame("game1", new ChessGame());
        dataAccess.findOrCreateGame("game2", new ChessGame());
        assert !dataAccess.getAllGames().isEmpty();

        ArrayList<GameData> actual = classUnderTest.getGames();

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.stream().filter(g -> g.gameName().equals("game2")).count());
        assertEquals(1, actual.stream().filter(g -> g.gameName().equals("game1")).count());
    }

    @Test
    public void joinGameSetsWhitePlayer()
    {
        UpsertGameResult game = dataAccess.findOrCreateGame("zeppo", new ChessGame());
        assert game.whiteUsername() == null;
        assert game.blackUsername() == null;
        int gamedID = game.gameID();

        boolean actual = classUnderTest.joinGame(gamedID, ChessGame.TeamColor.WHITE, "hithere");

        assertTrue(actual);
        assertEquals(1,
                dataAccess.getAllGames().stream()
                          .filter(g->g.gameID()== gamedID)
                          .filter(g-> g.whiteUsername().equals("hithere"))
                          .count());
        assertEquals(0,
                dataAccess.getAllGames().stream()
                        .filter(g->g.gameID()== gamedID)
                        .filter(g->g.blackUsername() != null && !g.blackUsername().isEmpty())
                        .count());
    }

    @Test
    public void joinGameSetsBlackPlayer()
    {
        UpsertGameResult game = dataAccess.findOrCreateGame("turkey", new ChessGame());
        assert game.whiteUsername() == null;
        assert game.blackUsername() == null;
        int gamedID = game.gameID();

        boolean actual = classUnderTest.joinGame(gamedID, ChessGame.TeamColor.BLACK, "siam");

        assertTrue(actual);
        assertEquals(1,
                dataAccess.getAllGames().stream()
                        .filter(g->g.gameID()== gamedID)
                        .filter(g-> g.blackUsername().equals("siam"))
                        .count());
        assertEquals(0,
                dataAccess.getAllGames().stream()
                        .filter(g->g.gameID()== gamedID)
                        .filter(g->g.whiteUsername() != null && !g.whiteUsername().isEmpty())
                        .count());
    }

    @Test
    public void resetClearsAllGamesIfSomePresent()
    {
        dataAccess.findOrCreateGame("game01", new ChessGame());
        dataAccess.findOrCreateGame("game02", new ChessGame());
        assert !dataAccess.getAllGames().isEmpty();

        classUnderTest.reset();

        assertTrue(dataAccess.getAllGames().isEmpty());
    }

    @Test
    public void resetDoesNothingIfNoSavedGames()
    {
        assert dataAccess.getAllGames().isEmpty();

        classUnderTest.reset();

        assertTrue(dataAccess.getAllGames().isEmpty());
    }
}
