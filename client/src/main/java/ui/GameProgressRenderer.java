package ui;

import ui.data.AppState;

public class GameProgressRenderer {
    private final AppState appState;
    private final BufferedRenderer baseRenderer;

    public GameProgressRenderer(AppState appState, BufferedRenderer baseRenderer) {
        this.appState = appState;
        this.baseRenderer = baseRenderer;
    }

    public void showPlayState() {
        if(appState.getCurrentGame().isOver()) {
            baseRenderer.userActionComplete("Game over: %s wins!".formatted(appState.getCurrentGame().getWinner()));
        } else {
            String activePlayerUsername = appState.getCurrentGame().usernameForCurrentTurn();
            if(appState.currentUsername().equals(activePlayerUsername)) {
                baseRenderer.myTurn();
            } else {
                baseRenderer.waitingOnPlayer(activePlayerUsername);
            }
        }
    }
}
