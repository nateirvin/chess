package ui;

public class InvalidMenuCommand implements MenuCommand {
    @Override
    public void execute(String... arguments) {
        print();
    }

    private static void print() {
        System.out.println(">>> Unknown command");
        HelpCommand.printHelp();
    }
}
