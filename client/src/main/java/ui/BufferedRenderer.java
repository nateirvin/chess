package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;
import model.GameData;
import websocket.messages.ServerMessage;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BufferedRenderer implements Closeable
{
    private final ConsoleReader reader;
    private final ChessBoardRenderer boardRenderer;
    private final GameListRenderer gameListRenderer;
    private final DisplaySink writer;

    private volatile boolean waitingForUpdate;
    private final Object writerLock = new Object();
    private String currentPrompt;
    private BoardUpdate boardUpdate;
    private final Queue<PlayerUpdate> playerUpdates;

    public BufferedRenderer(ConsoleReader reader, DisplaySink writer) {
        this.reader = reader;
        this.writer = writer;
        this.gameListRenderer = new GameListRenderer(writer);
        this.boardRenderer = new ChessBoardRenderer(writer, ColorScheme.trueColor());

        this.boardUpdate = null;
        this.playerUpdates = new LinkedList<>();
    }

    public void promptAndWait(String prompt)
    {
        assert prompt != null && !prompt.isBlank();
        this.currentPrompt = prompt;

        synchronized (writerLock) {
            renderPendingUpdates();
            showPrompt();
        }

        reader.read();
        currentPrompt = null;

        synchronized (writerLock) {
            renderPendingUpdates();
        }
    }

    public String firstWordEntered() {
        return reader.firstToken();
    }

    public String[] allButFirstEnteredWord() {
        return reader.allButFirstToken();
    }

    public void userActionComplete(String message) {
        synchronized (writerLock) {
            writer.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
            writer.println(message);
            writer.print(EscapeSequences.RESET_TEXT_COLOR);
            writer.println();
            renderPendingUpdates();
        }
    }

    private void renderPendingUpdates() {
        if(this.boardUpdate != null) {
            renderBoard(boardUpdate.board, boardUpdate.color, null);
            this.boardUpdate = null;
        }
        while (!playerUpdates.isEmpty()) {
            PlayerUpdate playerUpdate = playerUpdates.poll();
            if(playerUpdate.updateType == ServerMessage.ServerMessageType.NOTIFICATION) {
                renderMessage(playerUpdate.message);
            } else {
                renderError(playerUpdate.message);
            }
        }
    }

    public void asyncUpdate(String message) {
        if(reader.isWaiting()) {
            synchronized (writerLock) {
                renderMessage(message);
                showPrompt();
            }
        } else {
            playerUpdates.add(new PlayerUpdate(ServerMessage.ServerMessageType.NOTIFICATION, message));
        }
    }

    private void renderMessage(String message) {
        writer.println();
        writer.println();
        writer.println("* " + message);
        writer.println();
    }

    public void helpMenuStart() {
        synchronized (writerLock) {
            writer.println();
            writer.println("Available commands:");
        }
    }

    public void helpMenuItem(String commandPattern, String explanation) {
        synchronized (writerLock) {
            writer.printf("  %s : %s%n", commandPattern, explanation);
        }
    }

    public void helpMenuEnd() {
        synchronized (writerLock) {
            writer.println();
        }
    }

    public void waitForBoard() {
        waitingForUpdate = true;
        writer.print("PLease wait...");

        while(this.boardUpdate == null && waitingForUpdate) {
            Thread.onSpinWait();
        }
        writer.println();
        waitingForUpdate = false;

        synchronized (writerLock) {
            renderPendingUpdates();
        }
    }

    public void updateBoard(ChessBoard board, ChessGame.TeamColor viewerColor) {
        if(reader.isWaiting()) {
            synchronized (writerLock) {
                writer.println();
                renderBoard(board, viewerColor, null);
                showPrompt();
            }
        } else {
            this.boardUpdate = new BoardUpdate(board, viewerColor);
        }
    }

    private void showPrompt() {
        writer.print(currentPrompt.trim());
        writer.print(" ");
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor) {
        board(board, viewerColor, null);
    }

    public void board(ChessBoard board, ChessGame.TeamColor viewerColor, ArrayList<ChessPosition> highlights) {
        synchronized (writerLock) {
            renderBoard(board, viewerColor, highlights);
        }
    }

    private void renderBoard(ChessBoard board, ChessGame.TeamColor viewerColor, ArrayList<ChessPosition> highlights) {
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
        synchronized (writerLock) {
            if(reader.isWaiting()) {
                writer.println();
                renderMyTurn();
                writer.println();
                showPrompt();
            } else {
                renderMyTurn();
            }
        }
    }

    private void renderMyTurn() {
        writer.print("It is ");
        writer.print(EscapeSequences.SET_TEXT_BOLD);
        writer.print("your");
        writer.print(EscapeSequences.RESET_TEXT_BOLD_FAINT);
        writer.println(" turn");
        writer.println();
    }

    public void waitingOnPlayer(String username) {
        String message;
        if(username != null && !username.isEmpty()) {
            message = "Waiting for %s's move...".formatted(username);
        } else {
            message = "Waiting for another player to join";
        }

        if(reader.isWaiting()) {
            synchronized (writerLock){
                writer.println();
                writer.println(message);
                writer.println();
                showPrompt();
            }
        } else {
            playerUpdates.add(new PlayerUpdate(ServerMessage.ServerMessageType.NOTIFICATION, message));
        }
    }

    public void gamesListWithAltText(ArrayList<GameData> games) {
        synchronized (writerLock) {
            gameListRenderer.showGamesListWithAlternateText(games,"No games yet; use the 'create' command to start one!");
            writer.println();
        }
    }

    public void error(String message) {
        synchronized (writerLock) {
            renderError(message);
        }
    }

    public void callbackError(String errorMessage) {
        if(reader.isWaiting()) {
            synchronized (writerLock) {
                writer.println();
                renderError(errorMessage);
                showPrompt();
            }
        } else if(waitingForUpdate) {
          writer.println();
          renderError(errorMessage);
          waitingForUpdate = false;
        } else {
            playerUpdates.add(new PlayerUpdate(ServerMessage.ServerMessageType.ERROR, errorMessage));
        }
    }

    private void renderError(String message) {
        writer.print(EscapeSequences.SET_TEXT_COLOR_RED);
        writer.printf(">>> %s%n", message);
        writer.println();
        writer.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private record BoardUpdate(ChessBoard board, ChessGame.TeamColor color) {
    }

    private record PlayerUpdate(ServerMessage.ServerMessageType updateType, String message) {
    }
}
