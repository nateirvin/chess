package ui.menu;

import model.UserEntryResult;

public class GameScopedCommandHandler {
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
