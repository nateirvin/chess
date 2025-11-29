package model;

import chess.ChessBoard;
import chess.ChessGame;

public class GameData
{
    private final int gameID;
    private final String gameName;
    private String whiteUsername;
    private String blackUsername;
    private String resignedUsername;
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

    public void concededBy(String resignedUsername) {
        this.resignedUsername = resignedUsername;
    }

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

    public boolean hasPlayer(String username) {
        return whiteUsername != null && whiteUsername.equals(username)
               ||
               blackUsername != null && blackUsername.equals(username);
    }

    public boolean isThisPlayersTurn(String username) {
        ChessGame.TeamColor whoseTurn = getGame().getTeamTurn();
        if(whoseTurn == ChessGame.TeamColor.WHITE) {
            return whiteUsername != null && whiteUsername.equals(username);
        }
        if(whoseTurn == ChessGame.TeamColor.BLACK) {
            return blackUsername != null && blackUsername.equals(username);
        }
        return false;
    }

    public boolean isOver() {
        if(resignedUsername != null) {
            return true;
        }

        ChessBoard board = getGame().getBoard();
        if(board.isInCheckmate(ChessGame.TeamColor.WHITE) ||
           board.isInCheckmate(ChessGame.TeamColor.BLACK) ||
           board.isInStalemate(ChessGame.TeamColor.WHITE) ||
           board.isInStalemate(ChessGame.TeamColor.BLACK))
        {
            return true;
        }

        return false;
    }
}