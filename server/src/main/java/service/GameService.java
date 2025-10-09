package service;

import dataaccess.GameDataAccess;
import model.CreateGameRequest;
import model.GameData;
import model.UpsertGameResult;

public class GameService
{
    private final GameDataAccess dataAccess;

    public GameService(GameDataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(CreateGameRequest gameData)
    {
        UpsertGameResult upsertResult = dataAccess.findOrCreate(gameData.gameName());
        if(!upsertResult.isNew())
        {
            throw new AlreadyTakenException("game", gameData.gameName());
        }
        return upsertResult;
    }
}
