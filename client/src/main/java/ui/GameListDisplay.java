package ui;

import model.GameData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class GameListDisplay {
    private final AppState appState;
    private final ServerFacade serverFacade;

    private ArrayList<GameData> games;

    public GameListDisplay(AppState appState, ServerFacade serverFacade) {
        this.appState = appState;
        this.serverFacade = serverFacade;
    }

    public void showGamesList()
    {
        showGamesListWithAlternateText(null);
    }

    public void showGamesListWithAlternateText(String altText) {
        loadGames();

        if(!games.isEmpty()) {
            System.out.println("Games:");

            for (int i = 0; i < games.size(); i++)
            {
                GameData game = games.get(i);
                int gameNumber = i + 1;
                System.out.printf("%d. %s (white: %s, white: %s)%n",
                        gameNumber, game.gameName(), game.whiteUsername(), game.blackUsername());
            }
        }
        else if(altText != null && !altText.isEmpty()) {
            System.out.println(altText);
        }
    }

    public int getGameIdFromNumber(int number) throws IndexOutOfBoundsException {
        return getGames().get(number - 1).gameID();
    }

    private void loadGames() {
        try {
            ArrayList<GameData> games = serverFacade.getAllGames(appState.getAuthToken());
            games.sort(Comparator.comparing(GameData::gameName));
            this.games = games;
        } catch (HttpFailureException | IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get games list", e);
        }
    }

    public GameData getGameFromNumber(int gameId) {
        return getGames().stream()
                .filter(g -> g.gameID() == gameId)
                .findFirst()
                .orElse(null);
    }

    private ArrayList<GameData> getGames() {
        if(this.games == null) {
            throw new IllegalStateException("You must list the games in order to know the game numbers.");
        }
        return this.games;
    }
}
