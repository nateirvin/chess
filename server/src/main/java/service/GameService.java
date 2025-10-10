package service;

import dataaccess.GameDataAccess;
import model.CreateGameRequest;
import model.GameData;
import model.UpsertGameResult;

import java.util.ArrayList;

public class GameService
{
    private final GameDataAccess dataAccess;

    public GameService(GameDataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(CreateGameRequest gameData)
    {
        UpsertGameResult upsertResult = dataAccess.findOrCreateGame(gameData.gameName());
        if(!upsertResult.isNew())
        {
            throw new AlreadyTakenException("game", gameData.gameName());
        }
        return upsertResult;
    }

    public ArrayList<GameData> getGames() {
        return dataAccess.getAllGames();
    }

    public void reset() {
        dataAccess.deleteAllGames();
    }
}
