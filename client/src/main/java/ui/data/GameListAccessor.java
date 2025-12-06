package ui.data;

import model.GameData;
import model.UserEntryResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public final class GameListAccessor {
    private final AppState appState;
    private final ServerFacade serverFacade;

    public GameListAccessor(AppState appState, ServerFacade serverFacade) {
        this.appState = appState;
        this.serverFacade = serverFacade;
    }
    
    private ArrayList<GameData> games;

    public UserEntryResult<GameData> getGameByNumber(int number) {
        if(games == null) {
            return new UserEntryResult<>("You must list the games before you can select one.");
        }
        if(number >= 1 && number <= games.size()) {
            GameData gameData = games.get(number - 1);
            return new UserEntryResult<>(gameData);
        }
        return new UserEntryResult<>("Invalid game number.");
    }

    public ArrayList<GameData> loadGames() {
        try {
            ArrayList<GameData> games = serverFacade.getAllGames(appState.getAuthToken());
            games.sort(Comparator.comparing(GameData::gameName));
            this.games = games;
            return games;
        } catch (HttpFailureException | IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get games list", e);
        }
    }
}
