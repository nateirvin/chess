package dataaccess;

import model.UpsertGameResult;

public interface GameDataAccess
{
    UpsertGameResult findOrCreate(String name);
}
