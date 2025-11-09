package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardRenderer
{
    private final String borderBackgroundColor;
    private final String borderTextColor;
    private final String lightSquareBackgroundColor;
    private final String darkSquareBackgroundColor;
    private final String player1TextColor;
    private final String player2TextColor;

    public ChessBoardRenderer() {
        borderBackgroundColor = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        borderTextColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
        lightSquareBackgroundColor = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        darkSquareBackgroundColor = EscapeSequences.SET_BG_COLOR_BLACK;
        player1TextColor = EscapeSequences.SET_TEXT_COLOR_RED;
        player2TextColor = EscapeSequences.SET_TEXT_COLOR_BLUE;
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

        System.out.println();

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

        System.out.println();
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
        System.out.print(borderBackgroundColor);
        System.out.print(borderTextColor);
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
            backgroundColor = lightSquareBackgroundColor;
        } else {
            backgroundColor = darkSquareBackgroundColor;
        }
        System.out.print(backgroundColor);
    }

    private void setPieceColor(ChessGame.TeamColor teamColor) {
        String textColor;
        if(teamColor == ChessGame.TeamColor.WHITE) {
            textColor = player1TextColor;
        } else {
            textColor = player2TextColor;
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
