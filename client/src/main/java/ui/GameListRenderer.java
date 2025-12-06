package ui;

import model.GameData;
import java.util.ArrayList;

public class GameListRenderer {
    private final DisplaySink writer;

    public GameListRenderer(DisplaySink writer) {
        this.writer = writer;
    }

    public void showGamesListWithAlternateText(ArrayList<GameData> games, String altText) {
        if(!games.isEmpty()) {
            writer.println("Games:");

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

                writer.printf("%d. %s (white: %s, black: %s)%n",
                                  gameNumber, game.gameName(), whiteUsername, blackUsername);
            }
        }
        else if(altText != null && !altText.isEmpty()) {
            writer.println(altText);
        }
    }
}
