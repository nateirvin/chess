package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.data.BoardColumn;

import java.util.ArrayList;

public class ChessBoardRenderer
{
    private final ColorScheme colors;

    public ChessBoardRenderer(ColorScheme colors) {
        this.colors = colors;
    }

    private enum SquareColor {
        LIGHT,
        DARK
    }

    public ColorScheme getColorScheme() {
        return colors;
    }

    public void render(ChessBoard board, ChessGame.TeamColor perspective, ArrayList<ChessPosition> highlights)
    {
        int rowStart;
        int rowIncrement;
        int colStart;

        if(perspective == ChessGame.TeamColor.WHITE) {
            rowStart = 8;
            rowIncrement = -1;
            colStart = 1;
        } else {
            rowStart = 1;
            rowIncrement = 1;
            colStart = 8;
        }

        setFontWeight();

        printColumnGuide(colStart, rowIncrement);

        SquareColor squareColor = SquareColor.LIGHT;

        for(int row = rowStart; row >= 1 && row <= 8; row += rowIncrement)
        {
            startBorder();
            printRowNumber(row);
            endBorder();

            for(int col = colStart; col >= 1 && col <= 8; col += rowIncrement * -1)
            {
                printSquare(board, new Positions(new ChessPosition(row, col), highlights), squareColor);
                squareColor = squareColor == SquareColor.LIGHT ? SquareColor.DARK : SquareColor.LIGHT;
            }
            squareColor = squareColor == SquareColor.LIGHT ? SquareColor.DARK : SquareColor.LIGHT;

            startBorder();
            printRowNumber(row);
            endBorder();

            endLine();
        }

        printColumnGuide(colStart, rowIncrement);

        resetDisplayColors();
        resetFontWeight();
    }

    public void render(ChessBoard board, ChessGame.TeamColor perspective)
    {
        render(board, perspective, null);
    }

    private void setFontWeight() {
        System.out.print(EscapeSequences.SET_TEXT_BOLD);
    }

    private void printColumnGuide(int colStart, int rowIncrement)
    {
        startBorder();
        System.out.print("    ");

        for(int col = colStart; col >= 1 && col <= 8; col += rowIncrement * -1)
        {
            System.out.print(BoardColumn.numberToLetter(col));
            System.out.print("  ");
        }
        System.out.print("  ");

        endBorder();
        endLine();
    }

    private void startBorder() {
        System.out.print(colors.forBorderBackground());
        System.out.print(colors.forBorderText());
    }

    private static void printRowNumber(int row) {
        System.out.print(" ");
        System.out.print(row);
        System.out.print(" ");
    }

    private void printSquare(ChessBoard board, Positions positions, SquareColor squareColor)
    {
        String backgroundColor;
        if(positions.isHighlightedPiece()) {
            backgroundColor = colors.forAggressorBackground();
        } else {
            if(squareColor == SquareColor.LIGHT) {
                if(positions.isHighlightedSquare()) {
                    backgroundColor = colors.forLightHighlightSquare();
                } else {
                    backgroundColor = colors.forLightSquareBackground();
                }
            } else {
                if(positions.isHighlightedSquare()){
                    backgroundColor = colors.forDarkHighlightSquare();
                } else {
                    backgroundColor = colors.forDarkSquareBackground();
                }
            }
        }

        System.out.print(backgroundColor);

        System.out.print(" ");

        ChessPiece piece = board.getPiece(positions.drawPosition);
        if(piece != null) {

            String textColor;

            if(positions.isHighlightedPiece()) {
                textColor = colors.forAggressorText();
            } else {
                ChessGame.TeamColor teamColor = piece.getTeamColor();
                if(teamColor == ChessGame.TeamColor.WHITE) {
                    textColor = colors.forPlayer1Text();
                } else {
                    textColor = colors.forPlayer2Text();
                }
            }

            System.out.print(textColor);

            System.out.print(piece.shortCode().toUpperCase());
        } else {
            System.out.print(" ");
        }

        System.out.print(" ");
        endSquare();
    }

    private void endSquare() {
        resetDisplayColors();
    }

    private void endBorder() {
        resetDisplayColors();
    }

    private static void endLine() {
        System.out.println();
    }

    private void resetDisplayColors() {
        System.out.print(EscapeSequences.RESET_BG_COLOR);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private void resetFontWeight() {
        System.out.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    public record Positions(ChessPosition drawPosition, ArrayList<ChessPosition> highlightPositions) {
        public boolean isHighlightedPiece() {
            return this.drawPosition.equals(this.first());
        }

        private ChessPosition first() {
            if (highlightPositions != null && !highlightPositions.isEmpty()) {
                return highlightPositions.getFirst();
            }
            return null;
        }

        public boolean isHighlightedSquare() {
            if(highlightPositions != null) {
                for (int i = 1 ; i < highlightPositions.size(); i++) {
                    if(highlightPositions.get(i).equals(drawPosition)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
