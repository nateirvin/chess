import ui.*;
import ui.data.AppState;
import ui.data.ServerFacade;
import ui.menu.MenuCommandHandler;
import ui.menu.MenuCommandHandlerFactory;
import util.SerializerFactory;
import java.util.logging.*;

public class Main
{
    private static AppState appState;
    private static MenuCommandHandlerFactory menuCommandFactory;

    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");

        appState = new AppState();

        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger("default");

        try (ServerFacade serverFacade = new ServerFacade(new SerializerFactory().getGson());
             ConsoleReader consoleReader = new ConsoleReader())
        {
            GameListRenderer gameListRenderer = new GameListRenderer(appState, serverFacade);
            menuCommandFactory = new MenuCommandHandlerFactory(appState, logger, serverFacade, gameListRenderer);

            logger.addHandler(new FileHandler("chess-app.log", true));
            serverFacade.bindTo("localhost", 8080);

            runUsing(consoleReader);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Critical failure", e);
            System.out.println("A critical error has occurred.");
        }
    }

    private static void runUsing(ConsoleReader consoleReader) {
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
                ErrorRenderer.print(errorMessage);
            }
        }
    }
}