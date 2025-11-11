package ui;

import chess.ChessGame;
import model.GameData;
import model.UserEntryResult;

public class ObserveGameCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    public ObserveGameCommandHandler(GameListDisplay displayer) {
        super(displayer);
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

        GameData game = displayer.getGameFromNumber(gameNumberResult.getValue());
        ChessBoardRenderer renderer = new ChessBoardRenderer(ColorScheme.example());
        renderer.render(game.getGame().getBoard(), ChessGame.TeamColor.WHITE);

        return null;
    }
}
