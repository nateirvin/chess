package ui.menu.game;

import ui.BufferedRenderer;
import ui.data.AppState;
import ui.menu.MenuCommandHandler;

public class RedrawCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final BufferedRenderer render;

    public RedrawCommandHandler(AppState appState, BufferedRenderer render) {
        this.appState = appState;
        this.render = render;
    }

    @Override
    public String execute(String... arguments) {
        render.board(appState.getBoard(), appState.getPlayer());
        return null;
    }
}
