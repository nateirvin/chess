import ui.*;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");

        AppState appState = new AppState();
        var menuCommandFactory = new MenuCommandFactory(appState, new ServerFacade());

        try (ConsoleReader consoleReader = new ConsoleReader()) {

            while (true) {
                System.out.print("CHESS [" + appState.currentUsername() + "] $ ");
                consoleReader.read();

                MenuCommand command;
                if (!appState.userIsLoggedIn()) {
                    command = menuCommandFactory.getPreloginCommand(consoleReader.firstToken());
                } else {
                    command = menuCommandFactory.getPostloginCommand(consoleReader.firstToken());
                }

                if (command == null)   //user selected to quit
                {
                    System.out.println("Have a great day!");
                    System.out.println();
                    return;
                }

                String errorMessage = command.execute(consoleReader.allButFirstToken());

                if (errorMessage != null) {
                    InvalidMenuCommand.print(errorMessage);
                }
            }
        } catch (Exception e) {
            //TOOD: better handling
            System.out.println(e);
        }
    }
}