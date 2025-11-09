package ui;

public class PostloginHelpCommandHandler extends HelpCommandHandler
{
    @Override
    protected void printCommands() {
        System.out.println("  create <game_name> : start a new game");
        System.out.println("  list : list all the games");
        System.out.println("  observe <game_id> : watch a game");
        System.out.println("  join <game_id> <white|black> : join a game as a player");
        System.out.println("  logout : disconnect from game server for now");
    }
}
