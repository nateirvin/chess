package ui;

import chess.ChessBoard;
import chess.ChessGame;
import ui.data.GameListAccessor;
import java.io.Closeable;
import java.io.IOException;

public class BufferedRenderer implements Closeable {
    private final ConsoleReader reader;
    private ChessBoardRenderer boardRenderer;
    private GameListRenderer gameListRenderer;

    private GameUpdate gameUpdate;

    public BufferedRenderer() {
        reader = new ConsoleReader();
    }

    public void using(GameListAccessor gameListAccessor) {
        this.boardRenderer = new ChessBoardRenderer(ColorScheme.example());
        this.gameListRenderer = new GameListRenderer(gameListAccessor);
    }

    public void promptAndWait(String context) {
        System.out.printf("CHESS [%s] $ ", context);
        reader.read();
    }

    public String firstWordEntered() {
        return reader.firstToken();
    }

    public String[] allButFirstEnteredWord() {
        return reader.allButFirstToken();
    }

    public void userActionComplete(String message) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
        System.out.println(message);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
    }

    public void update(String message) {
        System.out.println(message);
        System.out.println();
    }

    public void helpMenuStart() {
        System.out.println();
        System.out.println("Available commands:");
    }

    public void helpMenuItem(String commandPattern, String explanation) {
        System.out.printf("  %s : %s%n", commandPattern, explanation);
    }

    public void helpMenuEnd() {
        System.out.println();
    }

    public void waitForBoard() {
        System.out.print("PLease wait...");
        while(gameUpdate == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //TODO: log
            }
            System.out.print(".");
        }

        if(gameUpdate == null) {
            error("Board could not be loaded");
        } else {
            System.out.println();
            System.out.println();

            boardRenderer.render(gameUpdate.board, gameUpdate.color);
            gameUpdate = null;

            System.out.println();
        }
    }

    public void updateBoard(ChessBoard board, ChessGame.TeamColor viewerColor) {
        gameUpdate = new GameUpdate(board, viewerColor);
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor) {
        boardRenderer.render(board, viewerColor);
    }

    public void gamesList() {
        gameListRenderer.showGamesList();
    }

    public void gamesListWithAltText() {
        gameListRenderer.showGamesListWithAlternateText("No games yet; use the 'create' command to start one!");
        System.out.println();
    }

    public void error(String message) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.printf(">>> %s%n", message);
        System.out.println();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private record GameUpdate(ChessBoard board, ChessGame.TeamColor color) {
    }
}
