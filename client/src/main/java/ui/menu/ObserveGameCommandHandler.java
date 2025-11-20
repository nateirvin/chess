package ui.menu;

import chess.ChessGame;
import model.GameData;
import model.UserEntryResult;
import ui.ChessBoardRenderer;
import ui.ColorScheme;
import ui.GameListRenderer;

public class ObserveGameCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    public ObserveGameCommandHandler(GameListRenderer displayer) {
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

        GameData game = displayer.getGameByNumber(gameNumberResult.getValue());
        if(game == null) {
            return "No such game.";
        }

        ChessBoardRenderer renderer = new ChessBoardRenderer(ColorScheme.example());
        renderer.render(game.getGame().getBoard(), ChessGame.TeamColor.WHITE);

        return null;
    }
}
