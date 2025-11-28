package ui.menu;

import ui.BufferedRenderer;

public class PreloginHelpCommandHandler extends HelpCommandHandler {
    public PreloginHelpCommandHandler(BufferedRenderer render) {
        super(render);
    }

    @Override
    protected void printCommands() {
        render.helpMenuItem("register <username> <password> <email>","required to play");
        render.helpMenuItem("login <username> <password>", "start playing games");
    }
}
