package ui.menu.help;

import ui.BufferedRenderer;
import ui.data.AppState;

public class GameplayHelpCommandHandler extends HelpCommandHandler {

    private final AppState appState;

    public GameplayHelpCommandHandler(AppState appState, BufferedRenderer render) {
        super(render);
        this.appState = appState;
    }

    @Override
    protected void printCommands() {
        render.helpMenuItem("redraw", "Redraw the chess board");
        render.helpMenuItem("moves <piece>", "Highlight the legal moves for the specified piece");
        if(!appState.isObserving() && !appState.getCurrentGame().isOver()) {
            render.helpMenuItem("move <from> <to>", "Make specified move");
            render.helpMenuItem("resign", "Concede defeat");
        }
        render.helpMenuItem("leave", "Exit the game (not the app)");
    }
}
