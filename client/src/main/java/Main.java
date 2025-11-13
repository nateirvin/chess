import ui.*;
import util.SerializerFactory;
import java.util.logging.*;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");

        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger("default");

        try (ServerFacade serverFacade = new ServerFacade(new SerializerFactory().getGson());
             ConsoleReader consoleReader = new ConsoleReader())
        {
            AppState appState = new AppState();
            GameListDisplay gameListDisplay = new GameListDisplay(appState, serverFacade);
            var menuCommandFactory = new MenuCommandHandlerFactory(appState, logger, serverFacade, gameListDisplay);

            logger.addHandler(new FileHandler("chess-app.log", true));
            serverFacade.bindTo("localhost", 8080);

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
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Critical failure", e);
            System.out.println("A critical error has occurred.");
        }
    }
}