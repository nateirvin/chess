package model;

public class UpsertGameResult extends GameData
{
    private final boolean isNew;

    public UpsertGameResult(GameData data, boolean isNew)
    {
        super(data.gameID(), data.gameName(), data.whiteUsername(), data.blackUsername());
        this.isNew = isNew;
    }

    public boolean isNew() {
        return isNew;
    }
}
