package dataaccess;

import model.GameData;
import model.UpsertGameResult;

import java.util.ArrayList;

public interface GameDataAccess
{
    UpsertGameResult findOrCreateGame(String name);
    ArrayList<GameData> getAllGames();
    boolean setWhiteTeam(int gamedID, String username);
    boolean setBlackTeam(int gamedID, String username);
    void deleteAllGames();
}
