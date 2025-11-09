package ui;

import model.GameData;

import java.util.ArrayList;
import java.util.Comparator;

public class GameListDisplay {
    private final ServerFacade serverFacade;

    public GameListDisplay(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void showGamesList()
    {
        showGamesListWithAlternateText(null);
    }

    public void showGamesListWithAlternateText(String altText) {
        ArrayList<GameData> games = serverFacade.getAllGames();

        if(!games.isEmpty()) {
            System.out.println("Games:");

            games.sort(Comparator.comparing(GameData::gameName));
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
