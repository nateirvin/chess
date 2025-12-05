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
    private final DisplaySink writer;

    private volatile GameUpdate gameUpdate;
    private final Queue<String> asyncMessages;

    public BufferedRenderer(ConsoleReader reader, DisplaySink writer) {
        this.reader = reader;
        this.writer = writer;
        asyncMessages = new SynchronousQueue<>();
        gameListRenderer = new GameListRenderer(writer);
        boardRenderer = new ChessBoardRenderer(writer, ColorScheme.trueColor());
    }

    public void promptAndWait(String prompt) {
        asyncNotices();
        boardUpdates();

        if(prompt != null && !prompt.isBlank()) {
            writer.print(prompt.trim());
            writer.print(" ");
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
        writer.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
        writer.println(message);
        writer.print(EscapeSequences.RESET_TEXT_COLOR);
        writer.println();
    }

    public void notice(String message) {
        writer.println(message);
        writer.println();
    }

    public void asyncUpdate(String message) {
        synchronized (asyncMessages) {
            if(reader.isWaiting()) {
                writer.println("* " + message);
            } else {
                asyncMessages.add(message);
            }
        }
    }

    private void asyncNotices() {
        synchronized (asyncMessages) {
            while (!asyncMessages.isEmpty()) {
                String message = asyncMessages.poll();
                writer.println("* " + message);
            }
        }
    }

    public void helpMenuStart() {
        writer.println();
        writer.println("Available commands:");
    }

    public void helpMenuItem(String commandPattern, String explanation) {
        writer.printf("  %s : %s%n", commandPattern, explanation);
    }

    public void helpMenuEnd() {
        writer.println();
    }

    public void waitForBoard() {
        writer.print("PLease wait...");

        while(gameUpdate == null) {
            Thread.onSpinWait();
        }
        writer.println();

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

        if(reader.isWaiting()) {
            asyncNotices();
            boardUpdates();
        }
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor) {
        board(board, viewerColor, null);
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor, ArrayList<ChessPosition> highlights) {
        writer.println();

        boardRenderer.render(board, viewerColor, highlights);

        ColorScheme colors = boardRenderer.getColorScheme();
        if(!colors.areSelfExplanatory()) {
            writer.println();
            writer.print(EscapeSequences.SET_TEXT_ITALIC);

            writer.printf("%s = white team, %s = black team%n",
                    colors.player1TextColorName(),
                    colors.player2TextColorName());
            writer.print(EscapeSequences.RESET_TEXT_ITALIC);
        }

        writer.println();
    }

    public void myTurn() {
        writer.print("It is ");
        writer.print(EscapeSequences.SET_TEXT_BOLD);
        writer.print("your");
        writer.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
        writer.println(" turn");
        writer.println();
    }

    public void waitingOnPlayer(String username) {
        if(username != null && !username.isEmpty()) {
            writer.printf("Waiting for %s's move...%n", username);
        } else {
            writer.printf("Waiting for another player to join", "\n");
        }

        writer.println();
    }

    public void gamesList(ArrayList<GameData> games) {
        gameListRenderer.showGamesList(games);
    }

    public void gamesListWithAltText(ArrayList<GameData> games) {
        gameListRenderer.showGamesListWithAlternateText(games,"No games yet; use the 'create' command to start one!");
        writer.println();
    }

    public void error(String message) {
        writer.print(EscapeSequences.SET_TEXT_COLOR_RED);
        writer.printf(">>> %s%n", message);
        writer.println();
        writer.print(EscapeSequences.RESET_TEXT_COLOR);
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
