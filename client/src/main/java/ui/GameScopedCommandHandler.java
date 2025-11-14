package ui;

import model.UserEntryResult;

public class GameScopedCommandHandler {
    protected final GameListDisplay displayer;

    public GameScopedCommandHandler(GameListDisplay displayer) {
        this.displayer = displayer;
    }

    protected UserEntryResult<Integer> getGameNumber(String rawValue) {
        try
        {
            int gameNumber = Integer.parseInt(rawValue);
            int gameId = displayer.getGameIdFromNumber(gameNumber);
            return new UserEntryResult<>(gameId);
        }
        catch(NumberFormatException | IndexOutOfBoundsException ex)
        {
            return new UserEntryResult<>("Not a valid game number");
        }
    }
}
