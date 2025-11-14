package ui;

public class PreloginHelpCommandHandler extends HelpCommandHandler {
    @Override
    protected void printCommands() {
        System.out.println("  register <username> <password> <email> : required to play");
        System.out.println("  login <username> <password> : start playing games");
    }
}
