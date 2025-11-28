package ui.data;

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;
import model.GameData;

public class AppState
{
    public static final String GUEST_USERNAME = "guest";

    private AuthData userSession;
    private GameData game;
    private ChessGame.TeamColor player;

    public void setSession(AuthData userSession) {
        if(userSession == null) {
            throw new IllegalArgumentException();
        }
        if(userIsLoggedIn()) {
            throw new IllegalStateException("You must end the previous session before assigning a new one.");
        }
        this.userSession = userSession;
    }

    public void setGame(GameData game) {
        setGame(game, null);
    }

    public void setGame(GameData game, ChessGame.TeamColor playerColor) {
        if(game == null) {
            throw new IllegalArgumentException();
        }
        if(this.game != null) {
            throw new IllegalStateException("You must leave the current game before moving to a new one.");
        }
        this.game = game;
        this.player = playerColor;
    }

    public String currentUsername()
    {
        return userIsLoggedIn()
                ? userSession.username()
                : GUEST_USERNAME;
    }

    public String gameName() {
        return inGameplayMode()
                ? this.game.gameName()
                : null;
    }

    public boolean userIsLoggedIn() {
        return userSession != null;
    }

    public boolean inGameplayMode() {
        return this.game != null;
    }

    public boolean isObserving() {
        if (inGameplayMode()) {
            return this.player == null;
        }
        return false;
    }

    public String getAuthToken() {
        return userSession.authToken();
    }

    public AuthData getSession() {
        return userSession;
    }

    public ChessBoard getBoard() {
        if(inGameplayMode()) {
            return game.getGame().getBoard();
        }
        return null;
    }

    public ChessGame.TeamColor getPlayer() {
        if(!inGameplayMode()) {
            throw new IllegalStateException();
        }
        return this.player != null ? this.player : ChessGame.TeamColor.WHITE;
    }

    public void unsetGame() {
        this.game = null;
        this.player = null;
    }

    public void endSession() {
        userSession = null;
    }
}
