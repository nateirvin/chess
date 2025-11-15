package ui;

import model.GameData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class GameListAccessor {
    private final AppState appState;
    private final ServerFacade serverFacade;

    public GameListAccessor(AppState appState, ServerFacade serverFacade) {
        this.appState = appState;
        this.serverFacade = serverFacade;
    }
    
    private ArrayList<GameData> games;

    public GameData getGameByNumber(int number) {
        if(number >= 1 && number <= games.size()) {
            return getGames().get(number - 1);
        }
        return null;
    }

    protected ArrayList<GameData> loadGames() {
        try {
            ArrayList<GameData> games = serverFacade.getAllGames(appState.getAuthToken());
            games.sort(Comparator.comparing(GameData::gameName));
            this.games = games;
            return games;
        } catch (HttpFailureException | IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get games list", e);
        }
    }

    private ArrayList<GameData> getGames() {
        if(this.games == null) {
            throw new IllegalStateException("You must list the games in order to know the game numbers.");
        }
        return this.games;
    }
}
