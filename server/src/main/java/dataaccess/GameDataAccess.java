package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UpsertGameResult;

import java.util.ArrayList;

public interface GameDataAccess
{
    UpsertGameResult findOrCreateGame(String name, ChessGame gameInfo);
    GameData getGameById(int gameID);
    ArrayList<GameData> getAllGames();
    boolean setWhiteTeam(int gamedID, String username);
    boolean setBlackTeam(int gamedID, String username);
    void updateGame(GameData gameData);
    void deleteAllGames();
}
