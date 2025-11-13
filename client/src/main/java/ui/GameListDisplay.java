package ui;

import model.GameData;

public class GameListDisplay extends GameListAccessor {
    public GameListDisplay(AppState appState, ServerFacade serverFacade) {
        super(appState, serverFacade);
    }

    public void showGamesList()
    {
        showGamesListWithAlternateText(null);
    }

    public void showGamesListWithAlternateText(String altText) {
        var games = loadGames();

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
}
