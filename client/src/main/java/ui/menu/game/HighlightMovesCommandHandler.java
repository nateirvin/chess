package ui.menu.game;

import chess.*;
import model.UserEntryResult;
import ui.BufferedRenderer;
import ui.data.AppState;
import ui.menu.GameScopedCommandHandler;
import ui.menu.MenuCommandHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HighlightMovesCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler {
    public HighlightMovesCommandHandler(AppState appState, BufferedRenderer render) {
        super(appState, render, null);
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length != 1) {
            return "Invalid arguments";
        }

        UserEntryResult<ChessPosition> positionParseResult = getPosition(arguments[0].trim());
        if(!positionParseResult.success()) {
            return positionParseResult.getErrorMessage();
        }
        ChessPosition piecePosition = positionParseResult.getValue();

        ChessGame game = appState.getCurrentGame().getGame();
        ChessBoard board = game.getBoard();

        ChessPiece piece = board.getPiece(piecePosition);
        if(piece == null) {
            return "There is no piece at this position.";
        }

        Collection<ChessMove> moves = game.validMoves(piecePosition);

        ArrayList<ChessPosition> highlights = new ArrayList<>();
        highlights.add(piecePosition);
        List<ChessPosition> destinations = moves.stream().map(ChessMove::getEndPosition).toList();
        highlights.addAll(destinations);

        render.board(board, appState.getPlayer(), highlights);
        return null;
    }
}
