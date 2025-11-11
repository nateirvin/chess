import ui.*;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");

        AppState appState = new AppState();
        ServerFacade serverFacade = new ServerFacade();
        var menuCommandFactory = new MenuCommandHandlerFactory(appState, serverFacade, new GameListDisplay(serverFacade));

        try (ConsoleReader consoleReader = new ConsoleReader()) {

            while (true) {
                System.out.print("CHESS [" + appState.currentUsername() + "] $ ");
                consoleReader.read();

                MenuCommandHandler command;
                if (!appState.userIsLoggedIn()) {
                    command = menuCommandFactory.getPreLoginCommand(consoleReader.firstToken());
                } else {
                    command = menuCommandFactory.getPostLoginCommand(consoleReader.firstToken());
                }

                if (command == null)   //user selected to quit
                {
                    System.out.println("Have a great day!");
                    System.out.println();
                    return;
                }

                String errorMessage = command.execute(consoleReader.allButFirstToken());

                if (errorMessage != null) {
                    InvalidMenuCommandHandler.print(errorMessage);
                }
            }
        } catch (Exception e) {
            //TOOD: better handling
            System.out.println(e);
        }
    }
}