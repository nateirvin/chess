package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ui.ChessBoardRenderer;
import ui.ColorScheme;
import ui.ConsoleWriter;

import java.util.ArrayList;

public class ChessBoardRendererTests
{
    @Test
    public void smokeTestWhite()
    {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var classUnderTest = new ChessBoardRenderer(new ConsoleWriter(), ColorScheme.example());

        classUnderTest.render(board, ChessGame.TeamColor.WHITE);
    }

    @Test
    public void smokeTestBlack()
    {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var classUnderTest = new ChessBoardRenderer(new ConsoleWriter(), ColorScheme.lighter());

        classUnderTest.render(board, ChessGame.TeamColor.BLACK);
    }

    @Test
    public void smokeTestTrueColor() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        var classUnderTest = new ChessBoardRenderer(new ConsoleWriter(), ColorScheme.trueColor());

        ArrayList<ChessPosition> highlights = new ArrayList<>();
        highlights.add(new ChessPosition(2, 1));
        highlights.add(new ChessPosition(3, 1));
        highlights.add(new ChessPosition(4, 1));

        classUnderTest.render(board, ChessGame.TeamColor.WHITE, highlights);
    }

    @Test
    public void isHighlightedPieceReturnsTrue()
    {
        ChessPosition currentPosition = new ChessPosition(7, 8);
        ArrayList<ChessPosition> highlightPositions = new ArrayList<>();
        highlightPositions.add(new ChessPosition(7, 8));
        highlightPositions.add(new ChessPosition(5,8));
        highlightPositions.add(new ChessPosition(6,8));

        ChessBoardRenderer.Positions classUnderTest = new ChessBoardRenderer.Positions(currentPosition, highlightPositions);

        Assertions.assertTrue(classUnderTest.isHighlightedPiece());
    }
}
