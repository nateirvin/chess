package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

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

    public void render(ChessBoard board, ChessGame.TeamColor perspective)
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
                printSquare(board, row, col, squareColor);
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

    private void setFontWeight() {
        System.out.print(EscapeSequences.SET_TEXT_BOLD);
    }

    private void printColumnGuide(int colStart, int rowIncrement)
    {
        startBorder();
        System.out.print("    ");

        for(int col = colStart; col >= 1 && col <= 8; col += rowIncrement * -1)
        {
            System.out.print(getColumnLetter(col));
            System.out.print("  ");
        }
        System.out.print("  ");

        endBorder();
        endLine();
    }

    private char getColumnLetter(int columnNumber){
        if(columnNumber == 1) {
            return 'a';
        } else if(columnNumber == 2) {
            return 'b';
        } else if(columnNumber == 3) {
            return 'c';
        } else if(columnNumber == 4) {
            return 'd';
        } else if(columnNumber == 5) {
            return 'e';
        } else if(columnNumber == 6) {
            return 'f';
        } else if(columnNumber == 7) {
            return 'g';
        } else if(columnNumber == 8) {
            return 'h';
        } else {
            throw new IllegalArgumentException();
        }
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

    private void printSquare(ChessBoard board, int row, int col, SquareColor squareColor)
    {
        startSquare(squareColor);
        System.out.print(" ");

        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        if(piece != null) {
            setPieceColor(piece.getTeamColor());
            System.out.print(piece.shortCode().toUpperCase());
        } else {
            System.out.print(" ");
        }

        System.out.print(" ");
        endSquare();
    }

    private void startSquare(SquareColor squareColor) {
        String backgroundColor;
        if(squareColor == SquareColor.LIGHT) {
            backgroundColor = colors.forLightSquareBackground();
        } else {
            backgroundColor = colors.forDarkSquareBackground();
        }
        System.out.print(backgroundColor);
    }

    private void setPieceColor(ChessGame.TeamColor teamColor) {
        String textColor;
        if(teamColor == ChessGame.TeamColor.WHITE) {
            textColor = colors.forPlayer1Text();
        } else {
            textColor = colors.forPlayer2Text();
        }
        System.out.print(textColor);
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
}
