package client;

import chess.ChessBoard;
import chess.ChessGame;
import org.junit.jupiter.api.Test;
import ui.ChessBoardRenderer;

public class ChessBoardRendererTests
{
    @Test
    public void smokeTestWhite()
    {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var classUnderTest = new ChessBoardRenderer();

        classUnderTest.render(board, ChessGame.TeamColor.WHITE);
    }

    @Test
    public void smokeTestBlack()
    {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var classUnderTest = new ChessBoardRenderer();

        classUnderTest.render(board, ChessGame.TeamColor.BLACK);
    }
}
