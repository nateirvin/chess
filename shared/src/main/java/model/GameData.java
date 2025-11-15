package model;

import chess.ChessGame;

public class GameData
{
    private final int gameID;
    private final String gameName;
    private String whiteUsername;
    private String blackUsername;
    private ChessGame game;

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

    public void whiteUsername(String username) {
        this.whiteUsername = username;
    }

    public String blackUsername() {
        return blackUsername;
    }

    public void blackUsername(String username) {
        this.blackUsername = username;
    }

    public ChessGame getGame() { return game; }
    public void setGame(ChessGame game) { this.game = game; }

    public String usernameFor(ChessGame.TeamColor teamColor) {
        if(teamColor == ChessGame.TeamColor.WHITE) {
            return whiteUsername;
        } else if(teamColor == ChessGame.TeamColor.BLACK) {
            return blackUsername;
        }
        return null;
    }

    public void setPlayer(ChessGame.TeamColor teamColor, String username) {
        if(teamColor == ChessGame.TeamColor.WHITE) {
            this.whiteUsername = username;
        } else if(teamColor == ChessGame.TeamColor.BLACK) {
            this.blackUsername = username;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}