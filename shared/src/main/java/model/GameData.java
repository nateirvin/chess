package model;

public class GameData
{
    private final int gameID;
    private final String gameName;
    private final String whiteUsername;
    private final String blackUsername;

    public GameData(int gameID, String gameName)
    {
        this(gameID, gameName, null, null);
    }

    public GameData(int gameID, String gameName, String whiteUsername, String blackUsername)
    {
        this.gameID = gameID;
        this.gameName = gameName;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
    }

    public int gameID() {
        return gameID;
    }

    public String gameName() {
        return gameName;
    }

    public String whiteUsername() {
        return whiteUsername;
    }

    public String blackUsername() {
        return blackUsername;
    }
}