package dataaccess;

import model.GameData;
import model.UpsertGameResult;

import java.util.ArrayList;
import java.util.HashMap;

public class GameMemoryProvider implements GameDataAccess
{
    private static int lastId = 0;
    private final static HashMap<String, GameData> games = new HashMap<>();

    @Override
    public UpsertGameResult findOrCreateGame(String name)
    {
        if(games.containsKey(name))
        {
            return new UpsertGameResult(games.get(name), false);
        }
        else
        {
            GameData gameData = new GameData(++lastId, name);
            games.put(name, gameData);
            return new UpsertGameResult(gameData, true);
        }
    }

    @Override
    public ArrayList<GameData> getAllGames()
    {
        return new ArrayList<>(games.values());
    }

    @Override
    public void deleteAllGames()
    {
        games.clear();
    }
}
