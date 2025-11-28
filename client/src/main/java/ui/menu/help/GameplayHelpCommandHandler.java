package ui.menu.help;

import ui.BufferedRenderer;

public class GameplayHelpCommandHandler extends HelpCommandHandler {

    public GameplayHelpCommandHandler(BufferedRenderer render) {
        super(render);
    }

    @Override
    protected void printCommands() {
        render.helpMenuItem("moves <piece>", "Highlight the legal moves for the specified piece");
        render.helpMenuItem("move <from> <to>", "Make specified move");
        render.helpMenuItem("redraw", "Redraw the chess board");
        render.helpMenuItem("resign", "Concede defeat");
        render.helpMenuItem("leave", "Exit the game (not the app)");
    }
}
