package ui.data;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

public class AppState
{
    public static final String GUEST_USERNAME = "guest";

    private AuthData userSession;
    private final Object gameLock = new Object();
    private GameData game;
    private ChessGame.TeamColor player = null;

    public void setSession(AuthData userSession) {
        if(userSession == null) {
            throw new IllegalArgumentException();
        }
        if(userIsLoggedIn()) {
            throw new IllegalStateException("You must end the previous session before assigning a new one.");
        }
        this.userSession = userSession;
    }

    public void updateGame(GameData game) {
        if(game == null) {
            throw new IllegalArgumentException();
        }

        synchronized (gameLock) {
            if(this.game != null && this.game.gameID() != game.gameID()) {
                throw new IllegalStateException();
            }

            this.game = game;
        }
    }

    public boolean inGameplayMode() {
        return this.game != null;
    }

    public String gameName() {
        synchronized (gameLock) {
            return this.game != null ? this.game.gameName() : null;
        }
    }

    public GameData getCurrentGame() {
        synchronized (gameLock) {
            return game;
        }
    }

    public void unsetGame() {
        synchronized (gameLock) {
            this.game = null;
        }

        this.player = null;
    }

    public void setPlayer(ChessGame.TeamColor color) {
        this.player = color;
    }

    public String currentUsername()
    {
        return userIsLoggedIn()
                ? userSession.username()
                : GUEST_USERNAME;
    }

    public boolean userIsLoggedIn() {
        return userSession != null;
    }

    public boolean userIsObserver() {
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

    public ChessGame.TeamColor getPlayer() {
        if(!inGameplayMode()) {
            throw new IllegalStateException();
        }
        return this.player != null ? this.player : ChessGame.TeamColor.WHITE;
    }

    public void endSession() {
        userSession = null;
    }
}
