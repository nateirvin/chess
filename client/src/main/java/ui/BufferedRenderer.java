package ui;

import chess.ChessBoard;
import chess.ChessGame;
import ui.data.GameListAccessor;
import java.io.Closeable;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

public class BufferedRenderer implements Closeable {
    private final ConsoleReader reader;
    private ChessBoardRenderer boardRenderer;
    private GameListRenderer gameListRenderer;

    private boolean inPrompt;
    private GameInfo pendingBoard;
    private final Queue<ScreenMessage> pendingMessages;

    public BufferedRenderer() {
        reader = new ConsoleReader();
        pendingMessages = new SynchronousQueue<>();
    }

    public void using(GameListAccessor gameListAccessor) {
        this.boardRenderer = new ChessBoardRenderer(ColorScheme.example());
        this.gameListRenderer = new GameListRenderer(gameListAccessor);
    }

    public void promptAndWait(String context)
    {
        inPrompt = true;

        System.out.printf("CHESS [%s] $ ", context);
        reader.read();

        while (!pendingMessages.isEmpty()) {
            ScreenMessage message = pendingMessages.poll();
            if(message.isError) {
                printErrorMessage(message.text);
            } else {
                printNotification(message.text);
            }
        }

        if(pendingBoard != null) {
            boardRenderer.render(pendingBoard.data, pendingBoard.color);
            pendingBoard = null;
        }

        inPrompt = false;
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
        if(inPrompt) {
            pendingMessages.add(new ScreenMessage(message, false));
        } else {
            printNotification(message);
        }
    }

    private static void printNotification(String text) {
        System.out.println(text);
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

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor) {
        if(inPrompt) {
            this.pendingBoard = new GameInfo(board, viewerColor);
        } else {
            boardRenderer.render(board, viewerColor);
        }
    }

    public void gamesList() {
        gameListRenderer.showGamesList();
    }

    public void gamesListWithAltText() {
        gameListRenderer.showGamesListWithAlternateText("No games yet; use the 'create' command to start one!");
        System.out.println();
    }

    public void error(String text) {
        if(inPrompt) {
            pendingMessages.add(new ScreenMessage(text, true));
        } else {
            printErrorMessage(text);
        }
    }

    private static void printErrorMessage(String text) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.printf(">>> %s%n", text);
        System.out.println();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private record ScreenMessage(String text, boolean isError) {
    }

    private record GameInfo(ChessBoard data, ChessGame.TeamColor color) {
    }
}
