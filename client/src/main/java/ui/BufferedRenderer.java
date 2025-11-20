package ui;

import chess.ChessBoard;
import chess.ChessGame;
import ui.data.GameListAccessor;

public class BufferedRenderer {
    private ChessBoardRenderer boardRenderer;
    private GameListRenderer gameListRenderer;

    public void using(GameListAccessor gameListAccessor) {
        this.boardRenderer = new ChessBoardRenderer(ColorScheme.example());
        this.gameListRenderer = new GameListRenderer(gameListAccessor);
    }

    public void userActionComplete(String message) {
        System.out.println(message);
        System.out.println();
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
}
