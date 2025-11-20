package ui.menu;

import model.UserEntryResult;
import ui.GameListRenderer;

public class GameScopedCommandHandler {
    protected final GameListRenderer displayer;

    public GameScopedCommandHandler(GameListRenderer displayer) {
        this.displayer = displayer;
    }

    protected UserEntryResult<Integer> getGameNumber(String rawValue) {
        try
        {
            int gameNumber = Integer.parseInt(rawValue);
            return new UserEntryResult<>(gameNumber);
        }
        catch(NumberFormatException ex)
        {
            return new UserEntryResult<>("Not a valid game number");
        }
    }
}
