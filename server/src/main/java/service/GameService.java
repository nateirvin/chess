package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.GameDataAccess;
import model.CreateGameRequest;
import model.GameData;
import model.UpsertGameResult;
import java.util.ArrayList;

@SuppressWarnings("ClassCanBeRecord")
public class GameService
{
    private final GameDataAccess dataAccess;

    public GameService(GameDataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(CreateGameRequest gameData)
    {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessGame gameInfo = new ChessGame();
        gameInfo.setBoard(board);

        UpsertGameResult upsertResult = dataAccess.findOrCreateGame(gameData.gameName(), gameInfo);
        if(!upsertResult.isNew())
        {
            throw new AlreadyTakenException("game", gameData.gameName());
        }
        return upsertResult;
    }

    public ArrayList<GameData> getGames()
    {
        return dataAccess.getAllGames();
    }

    public boolean joinGame(int gamedID, ChessGame.TeamColor color, String username)
    {
        if(color == ChessGame.TeamColor.WHITE)
        {
            return dataAccess.setWhiteTeam(gamedID, username);
        }
        {
            return dataAccess.setBlackTeam(gamedID, username);
        }
    }

    public void reset()
    {
        dataAccess.deleteAllGames();
    }
}
