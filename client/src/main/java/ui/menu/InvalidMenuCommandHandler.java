package ui.menu;

public class InvalidMenuCommandHandler implements MenuCommandHandler {
    @Override
    public String execute(String... arguments) {
        print();
        return null;
    }

    private static void print() {
        print("Unknown command");
    }

    public static void print(String message) {
        System.out.println(">>> " + message);
        System.out.println();
    }
}
