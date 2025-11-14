package ui.menu;

import model.UserEntryResult;
import ui.GameListDisplay;

public class GameScopedCommandHandler {
    protected final GameListDisplay displayer;

    public GameScopedCommandHandler(GameListDisplay displayer) {
        this.displayer = displayer;
    }

    protected UserEntryResult<Integer> getGameNumber(String rawValue) {
        try
        {
            int gameNumber = Integer.parseInt(rawValue);
            return new UserEntryResult<>(gameNumber);
        }
        catch(NumberFormatException | IndexOutOfBoundsException ex)
        {
            return new UserEntryResult<>("Not a valid game number");
        }
    }
}
