package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import model.GameData;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

public class BufferedRenderer implements Closeable {
    private final ConsoleReader reader;
    private final ChessBoardRenderer boardRenderer;
    private final GameListRenderer gameListRenderer;

    private volatile GameUpdate gameUpdate;
    private final Queue<String> asyncMessages;

    public BufferedRenderer() {
        reader = new ConsoleReader();
        asyncMessages = new SynchronousQueue<>();
        gameListRenderer = new GameListRenderer();
        boardRenderer = new ChessBoardRenderer(ColorScheme.trueColor());
    }

    public void promptAndWait(String prompt) {
        asyncNotices();
        boardUpdates();

        if(prompt != null && !prompt.isBlank()) {
            System.out.print(prompt.trim());
            System.out.print(" ");
        }

        reader.read();

        asyncNotices();
        boardUpdates();
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
            Thread.onSpinWait();
        }
        System.out.println();

        boardUpdates();
    }

    private void boardUpdates() {
        if(gameUpdate != null)
        {
            if (gameUpdate.errorMessage == null) {
                board(gameUpdate.board, gameUpdate.color);
            } else {
                error(gameUpdate.errorMessage);
            }
            gameUpdate = null;
        }
    }

    public void updateBoard(ChessBoard board, ChessGame.TeamColor viewerColor) {
        gameUpdate = new GameUpdate(board, viewerColor, null);
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor) {
        board(board, viewerColor, null);
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor, ArrayList<ChessPosition> highlights) {
        System.out.println();

        boardRenderer.render(board, viewerColor, highlights);

        ColorScheme colors = boardRenderer.getColorScheme();
        if(!colors.areSelfExplanatory()) {
            System.out.println();
            System.out.print(EscapeSequences.SET_TEXT_ITALIC);

            System.out.printf("%s = white team, %s = black team%n",
                    colors.player1TextColorName(),
                    colors.player2TextColorName());
            System.out.print(EscapeSequences.RESET_TEXT_ITALIC);
        }

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

    public void waitingOnPlayer(String username) {
        if(username != null && !username.isEmpty()) {
            System.out.printf("Waiting on %s's move...%n", username);
        } else {
            System.out.println("Waiting for another player to join");
        }

        System.out.println();
    }

    public void gamesList(ArrayList<GameData> games) {
        gameListRenderer.showGamesList(games);
    }

    public void gamesListWithAltText(ArrayList<GameData> games) {
        gameListRenderer.showGamesListWithAlternateText(games,"No games yet; use the 'create' command to start one!");
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
