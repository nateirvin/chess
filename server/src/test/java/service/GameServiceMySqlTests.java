package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UpsertGameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.SerializerFactory;
import java.sql.SQLException;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceMySqlTests
{
    private GameDataAccess dataAccess;
    private GameService classUnderTest;

    @BeforeEach
    public void setup() throws DataAccessException
    {
        dataAccess = new GameMySqlProvider(new SerializerFactory().getGson());
        classUnderTest = new GameService(dataAccess);
        TestHelper.ensureDatabaseSetup();
    }

    @Test
    public void joinGameSetsWhitePlayerTurnForWhitePlayerJoin() throws SQLException, DataAccessException
    {
        ChessGame gameInfo = new ChessGame();
        gameInfo.setBoard(new ChessBoard());
        UpsertGameResult game = dataAccess.findOrCreateGame(UUID.randomUUID().toString(), gameInfo);
        assert game.whiteUsername() == null;
        assert game.blackUsername() == null;
        int gamedID = game.gameID();
        String newUserName = "hi_there";
        TestHelper.insertTestUser(newUserName);

        boolean actual = classUnderTest.joinGame(gamedID, ChessGame.TeamColor.WHITE, newUserName);

        assertTrue(actual);
        GameData actualGame = dataAccess.getAllGames().stream()
                                .filter(g -> g.gameID() == gamedID)
                                .findFirst().get();
        assertEquals(ChessGame.TeamColor.WHITE, actualGame.getGame().getTeamTurn());
        assertEquals(newUserName, actualGame.usernameForCurrentTurn());
        assertTrue(actualGame.isThisPlayersTurn(newUserName));
        assertFalse(actualGame.isThisPlayersTurn(newUserName+"other"));
    }

    @Test
    public void joinGameSetsWhitePlayerTurnForBlackPlayerJoin() throws SQLException, DataAccessException
    {
        ChessGame gameInfo = new ChessGame();
        gameInfo.setBoard(new ChessBoard());
        UpsertGameResult game = dataAccess.findOrCreateGame(UUID.randomUUID().toString(), gameInfo);
        assert game.whiteUsername() == null;
        assert game.blackUsername() == null;
        int gamedID = game.gameID();
        String newUserName = "hi_there";
        TestHelper.insertTestUser(newUserName);

        boolean actual = classUnderTest.joinGame(gamedID, ChessGame.TeamColor.BLACK, newUserName);

        assertTrue(actual);
        GameData actualGame = dataAccess.getAllGames().stream()
                .filter(g -> g.gameID() == gamedID)
                .findFirst().get();
        assertEquals(ChessGame.TeamColor.WHITE, actualGame.getGame().getTeamTurn());
        assertNull(actualGame.usernameForCurrentTurn());
        assertFalse(actualGame.isThisPlayersTurn(newUserName));
        assertFalse(actualGame.isThisPlayersTurn(newUserName));
    }
}
