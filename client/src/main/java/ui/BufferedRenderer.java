package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import ui.data.GameListAccessor;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

public class BufferedRenderer implements Closeable {
    private final ConsoleReader reader;
    private ChessBoardRenderer boardRenderer;
    private GameListRenderer gameListRenderer;

    private GameUpdate gameUpdate;
    private final Queue<String> asyncMessages;

    public BufferedRenderer() {
        reader = new ConsoleReader();
        asyncMessages = new SynchronousQueue<>();
    }

    public void using(GameListAccessor gameListAccessor) {
        this.boardRenderer = new ChessBoardRenderer(ColorScheme.example());
        this.gameListRenderer = new GameListRenderer(gameListAccessor);
    }

    public void promptAndWait(String context) {
        asyncNotices();

        System.out.printf("CHESS [%s] $ ", context);
        reader.read();

        asyncNotices();
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

    public void notice(String message) {
        System.out.println(message);
        System.out.println();
    }

    public void asyncUpdate(String message) {
        synchronized (asyncMessages) {
            asyncMessages.add(message);
        }
    }

    private void asyncNotices() {
        synchronized (asyncMessages) {
            while (!asyncMessages.isEmpty()) {
                String message = asyncMessages.poll();
                System.out.println("* " + message);
            }
        }
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
            } catch (InterruptedException ex) {
                error(ex.getMessage());
            }
            System.out.print(".");
        }
        System.out.println();

        if(gameUpdate == null) {
            error("Board could not be loaded");
        } else {
            if(gameUpdate.errorMessage != null) {
                error(gameUpdate.errorMessage);
                gameUpdate = null;
                return;
            }

            System.out.println();

            boardRenderer.render(gameUpdate.board, gameUpdate.color);
            gameUpdate = null;

            System.out.println();
        }
    }

    public void updateBoard(ChessBoard board, ChessGame.TeamColor viewerColor) {
        gameUpdate = new GameUpdate(board, viewerColor, null);
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor) {
        System.out.println();
        boardRenderer.render(board, viewerColor);
        System.out.println();
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor, ArrayList<ChessPosition> highlights) {
        System.out.println();
        boardRenderer.render(board, viewerColor, highlights);
        System.out.println();
    }

    public void myTurn() {
        System.out.print("It is ");
        System.out.print(EscapeSequences.SET_TEXT_BOLD);
        System.out.print("your");
        System.out.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.println(" turn");
        System.out.println();
    }

    public void waitingOnOpponent(String username) {
        System.out.printf("Waiting on %s's move...%n", username);
        System.out.println();
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

    public void callbackError(String errorMessage) {
        this.gameUpdate = new GameUpdate(null, null, errorMessage);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private record GameUpdate(ChessBoard board, ChessGame.TeamColor color, String errorMessage) {
    }
}
