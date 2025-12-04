package ui.menu;

import chess.ChessPosition;
import model.UserEntryResult;
import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.BoardColumn;

public abstract class GameScopedCommandHandler {
    protected final AppState appState;
    protected final BufferedRenderer render;

    public GameScopedCommandHandler(AppState appState, BufferedRenderer render) {
        this.appState = appState;
        this.render = render;
    }

    protected UserEntryResult<Integer> getGameNumber(String rawValue) {
        return getNumber(rawValue, "game number");
    }

    protected UserEntryResult<ChessPosition> getPosition(String rawAddress) {
        if(rawAddress.length() != 2) {
            return new UserEntryResult<>("Not a valid position.");
        }

        String columnLetter = rawAddress.substring(0,1);
        Integer columnNumber = BoardColumn.letterToNumber(columnLetter);
        if(columnNumber == null) {
            return new UserEntryResult<>("Not a valid position.");
        }

        String rawRowNumber = rawAddress.substring(1,2);
        UserEntryResult<Integer> rowNumberResult = getNumber(rawRowNumber, "row number");
        if(!rowNumberResult.success()) {
            return new UserEntryResult<>(rowNumberResult.getErrorMessage());
        }

        return new UserEntryResult<>(new ChessPosition(rowNumberResult.getValue(), columnNumber));
    }

    protected static UserEntryResult<Integer> getNumber(String rawValue, String entityName) {
        try
        {
            int number = Integer.parseInt(rawValue);
            return new UserEntryResult<>(number);
        }
        catch(NumberFormatException ex)
        {
            return new UserEntryResult<>("Not a valid " + entityName);
        }
    }

    protected void renderGameOverIfDone() {
        if(appState.getCurrentGame().isOver()) {
            render.update("Game over: %s wins!".formatted(appState.getCurrentGame().getWinnerUsername()));
        }
    }
}
