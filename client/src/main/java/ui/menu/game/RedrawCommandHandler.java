package ui.menu.game;

import ui.BufferedRenderer;
import ui.data.AppState;
import ui.menu.GameScopedCommandHandler;
import ui.menu.MenuCommandHandler;

public class RedrawCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    public RedrawCommandHandler(AppState appState, BufferedRenderer render) {
        super(appState, render, null);
    }

    @Override
    public String execute(String... arguments) {
        render.board(appState.getCurrentGame().getGame().getBoard(), appState.getPlayer());

        if(!displayGameOver()) {
            displayTurnPlayer();
        }

        return null;
    }
}
