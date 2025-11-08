package ui;

public class PreloginHelpCommand extends HelpCommand {
    @Override
    protected void specificCommands() {
        System.out.println("  register <username> <password> <email> : required to play");
        System.out.println("  login <username> <password> : start playing games");
    }
}
