package ui.menu;

import chess.ChessPosition;
import model.GameData;
import model.UserEntryResult;
import ui.BufferedRenderer;
import ui.data.AppState;
import chess.BoardColumn;
import ui.data.GameListAccessor;

public abstract class GameScopedCommandHandler {
    protected final AppState appState;
    protected final BufferedRenderer render;
    private final GameListAccessor gameListAccessor;

    public GameScopedCommandHandler(AppState appState, BufferedRenderer render, GameListAccessor gameListAccessor) {
        this.appState = appState;
        this.render = render;
        this.gameListAccessor = gameListAccessor;
    }

    public static UserEntryResult<Integer> getGameNumber(String rawValue) {
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

    protected static UserEntryResult<Integer> getNumber(String rawValue, String entityName)
    {
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

    protected UserEntryResult<GameData> fetchGame(String gameNumberRaw) {
        UserEntryResult<Integer> gameNumberResult = getGameNumber(gameNumberRaw);
        if(!gameNumberResult.success()) {
            return new UserEntryResult<>(gameNumberResult.getErrorMessage());
        }
        int gameNumber = gameNumberResult.getValue();

        UserEntryResult<GameData> gameQuery = gameListAccessor.getGameByNumber(gameNumber);
        if (!gameQuery.success()) {
            return new UserEntryResult<>(gameQuery.getErrorMessage());
        }

        GameData game = gameQuery.getValue();
        if (game == null) {
            return new UserEntryResult<>("No such game.");
        }

        return new UserEntryResult<>(game);
    }

    protected void displayTurnPlayer() {
        displayTurnPlayer(appState, render);
    }

    public static void displayTurnPlayer(AppState appState1, BufferedRenderer render1) {
        String activePlayerUsername = appState1.getCurrentGame().usernameForCurrentTurn();
        if(appState1.currentUsername().equals(activePlayerUsername)) {
            render1.myTurn();
        } else {
            render1.waitingOnPlayer(activePlayerUsername);
        }
    }

    protected boolean displayGameOver() {
        if(appState.getCurrentGame().isOver()) {
            render.userActionComplete("Game over: %s wins!".formatted(appState.getCurrentGame().getWinner()));
            return true;
        }
        return false;
    }
}
