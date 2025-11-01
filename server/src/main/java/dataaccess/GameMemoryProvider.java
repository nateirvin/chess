package dataaccess;

import model.GameData;
import model.UpsertGameResult;
import service.AlreadyTakenException;

import java.util.ArrayList;
import java.util.HashMap;

public class GameMemoryProvider implements GameDataAccess
{
    private static int lastId = 0;
    private final static HashMap<String, GameData> GAMES = new HashMap<>();

    @Override
    public UpsertGameResult findOrCreateGame(String name)
    {
        if(GAMES.containsKey(name))
        {
            return new UpsertGameResult(GAMES.get(name), false);
        }
        else
        {
            GameData gameData = new GameData(++lastId, name);
            GAMES.put(name, gameData);
            return new UpsertGameResult(gameData, true);
        }
    }

    @Override
    public ArrayList<GameData> getAllGames()
    {
        return new ArrayList<>(GAMES.values());
    }

    @Override
    public boolean setWhiteTeam(int gamedID, String username) {
        for (GameData game : GAMES.values()) {
            if(game.gameID() == gamedID) {
                if(game.whiteUsername() != null && !game.whiteUsername().equals(username)) {
                    throw new AlreadyTakenException("username", username);
                }
                game.whiteUsername(username);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setBlackTeam(int gamedID, String username) {
        for (GameData game : GAMES.values()) {
            if(game.gameID() == gamedID) {
                if(game.blackUsername() != null && !game.blackUsername().equals(username)) {
                    throw new AlreadyTakenException("username", username);
                }
                game.blackUsername(username);
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteAllGames()
    {
        GAMES.clear();
    }
}
