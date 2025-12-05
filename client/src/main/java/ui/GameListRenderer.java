package ui;

import model.GameData;
import java.util.ArrayList;

public class GameListRenderer {
    public void showGamesList(ArrayList<GameData> games)
    {
        showGamesListWithAlternateText(games,null);
    }

    public void showGamesListWithAlternateText(ArrayList<GameData> games, String altText) {
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
