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

    public ChessGame.TeamColor getColorForUser(String username) {
        if(username == null) {
            return null;
        }
        if(whiteUsername != null && whiteUsername.equals(username)) {
            return ChessGame.TeamColor.WHITE;
        }
        if(blackUsername != null && blackUsername.equals(username)) {
            return ChessGame.TeamColor.BLACK;
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

    public String usernameForCurrentTurn() {
        return usernameFor(getGame().getTeamTurn());
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
        if(board.isInCheckmateOrStalemate(ChessGame.TeamColor.WHITE) ||
           board.isInCheckmateOrStalemate(ChessGame.TeamColor.BLACK))
        {
            return true;
        }

        return false;
    }

    public String getWinnerUsername() {
        if(resignedUsername != null) {
            if(resignedUsername.equals(whiteUsername)) {
                return blackUsername;
            } else if(resignedUsername.equals(blackUsername)) {
                return whiteUsername;
            }
        } else {
            ChessBoard board = getGame().getBoard();
            if(board.isInCheckmateOrStalemate(ChessGame.TeamColor.WHITE)) {
                return blackUsername;
            } else if(board.isInCheckmateOrStalemate(ChessGame.TeamColor.BLACK)) {
                return whiteUsername;
            }
        }

        return null;
    }
}