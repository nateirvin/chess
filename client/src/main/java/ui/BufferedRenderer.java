package ui;

import chess.ChessBoard;
import chess.ChessGame;

public class BufferedRenderer {
    private final ChessBoardRenderer boardRenderer;

    public BufferedRenderer() {
        this.boardRenderer = new ChessBoardRenderer(ColorScheme.example());
    }

    public void userActionComplete(String message) {
        System.out.println(message);
        System.out.println();
    }

    public void error(String message) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.printf(">>> %s%n", message);
        System.out.println();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor) {
        boardRenderer.render(board, viewerColor);
    }
}
