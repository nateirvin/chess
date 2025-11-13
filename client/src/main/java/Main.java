import ui.*;
import util.SerializerFactory;
import java.io.IOException;
import java.util.logging.*;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");

        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger("default");
        AppState appState = new AppState();
        ServerFacade serverFacade = new ServerFacade(new SerializerFactory().getGson());
        var menuCommandFactory =
                new MenuCommandHandlerFactory(appState, logger, serverFacade, new GameListDisplay(appState, serverFacade));

        try (ConsoleReader consoleReader = new ConsoleReader())
        {
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
        finally
        {
            try {
                serverFacade.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "shutdown failure", e);
            }
        }
    }
}