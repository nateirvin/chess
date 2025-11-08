package ui;

public class HelpCommand implements MenuCommand {
    @Override
    public void execute(String... arguments) {
        printHelp();
    }

    public static void printHelp()
    {
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("  help : show this menu");
        System.out.println("  register <username> <password> <email> : required to play");
        System.out.println("  login <username> <password> : start playing games");
        System.out.println("  quit : exit the app");
        System.out.println();
    }
}
