package ui.menu.game;

import ui.BufferedRenderer;
import ui.data.AppState;
import ui.menu.MenuCommandHandler;

public class LeaveGameCommandHandler implements MenuCommandHandler {
    private final AppState appState;
    private final BufferedRenderer render;

    public LeaveGameCommandHandler(AppState appState, BufferedRenderer render) {
        this.appState = appState;
        this.render = render;
    }

    @Override
    public String execute(String... arguments) {
        //TODO: communicate this

        appState.unsetGame();

        render.userActionComplete("Thanks for playing!");
        return null;
    }
}
