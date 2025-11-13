package ui;

import model.GameData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class GameListDisplay {
    private final AppState appState;
    private final ServerFacade serverFacade;

    public GameListDisplay(AppState appState, ServerFacade serverFacade) {
        this.appState = appState;
        this.serverFacade = serverFacade;
    }

    public void showGamesList()
    {
        showGamesListWithAlternateText(null);
    }

    public void showGamesListWithAlternateText(String altText) {
        ArrayList<GameData> games = getAllGames();

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
        return getAllGames().get(number - 1).gameID();
    }

    private ArrayList<GameData> getAllGames() {
        try {
            ArrayList<GameData> games = serverFacade.getAllGames(appState.getAuthToken());
            games.sort(Comparator.comparing(GameData::gameName));
            return games;
        } catch (HttpFailureException | IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get games list", e);
        }
    }

    public GameData getGameFromNumber(int gameId) {
        return getAllGames().stream().filter(g -> g.gameID() == gameId).findFirst().get();
    }
}
