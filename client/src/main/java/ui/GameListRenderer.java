package ui;

import model.GameData;
import ui.data.GameListAccessor;
import java.util.ArrayList;

public class GameListRenderer {
    private final GameListAccessor accessor;

    public GameListRenderer(GameListAccessor accessor) {
        this.accessor = accessor;
    }

    public void showGamesList()
    {
        showGamesListWithAlternateText(null);
    }

    public void showGamesListWithAlternateText(String altText) {
        ArrayList<GameData> games = accessor.loadGames();

        if(!games.isEmpty()) {
            System.out.println("Games:");

            for (int i = 0; i < games.size(); i++)
            {
                GameData game = games.get(i);
                int gameNumber = i + 1;

                String whiteUsername = game.whiteUsername();
                String blackUsername = game.blackUsername();
                if(whiteUsername == null) {
                    whiteUsername = "(none)";
                }
                if(blackUsername == null) {
                    blackUsername = "(none)";
                }

                System.out.printf("%d. %s (white: %s, black: %s)%n",
                                  gameNumber, game.gameName(), whiteUsername, blackUsername);
            }
        }
        else if(altText != null && !altText.isEmpty()) {
            System.out.println(altText);
        }
    }
}
