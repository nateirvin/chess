package ui.menu.game;

import ui.BufferedRenderer;
import ui.GameProgressRenderer;
import ui.data.AppState;
import ui.menu.GameScopedCommandHandler;
import ui.menu.MenuCommandHandler;

public class RedrawCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    private final GameProgressRenderer extendedRenderer;

    public RedrawCommandHandler(AppState appState, BufferedRenderer render) {
        super(appState, render, null);
        this.extendedRenderer = new GameProgressRenderer(appState, render);
    }

    @Override
    public String execute(String... arguments) {
        render.board(appState.getCurrentGame().getGame().getBoard(), appState.getPlayer());
        extendedRenderer.showPlayState();
        return null;
    }
}
