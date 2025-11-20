package ui.menu;

import chess.ChessGame;
import model.GameData;
import model.UserEntryResult;
import ui.BufferedRenderer;
import ui.data.GameListAccessor;

public class ObserveGameCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    private final GameListAccessor gameListAccessor;
    private final BufferedRenderer render;

    public ObserveGameCommandHandler(GameListAccessor gameListAccessor, BufferedRenderer render) {
        this.gameListAccessor = gameListAccessor;
        this.render = render;
    }

    @Override
    public String execute(String... arguments)
    {
        if (arguments.length != 1) {
            return "Invalid arguments";
        }

        UserEntryResult<Integer> gameNumberResult = getGameNumber(arguments[0]);
        if (!gameNumberResult.success()) {
            return gameNumberResult.getErrorMessage();
        }

        GameData game = gameListAccessor.getGameByNumber(gameNumberResult.getValue());
        if(game == null) {
            return "No such game.";
        }

        render.board(game.getGame().getBoard(), ChessGame.TeamColor.WHITE);

        return null;
    }
}
