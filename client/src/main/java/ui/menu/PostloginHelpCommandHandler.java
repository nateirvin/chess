package ui.menu;

import ui.BufferedRenderer;

public class PostloginHelpCommandHandler extends HelpCommandHandler
{
    public PostloginHelpCommandHandler(BufferedRenderer render) {
        super(render);
    }

    @Override
    protected void printCommands() {
        render.helpMenuItem("create <game_name>", "start a new game");
        render.helpMenuItem("list", "list all the games");
        render.helpMenuItem("observe <game_id>", "watch a game");
        render.helpMenuItem("join <game_id> <white|black>", "join a game as a player");
        render.helpMenuItem("logout", "disconnect from game server for now");
    }
}
