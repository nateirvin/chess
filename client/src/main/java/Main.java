import com.google.gson.Gson;
import ui.*;
import ui.data.AppState;
import ui.data.GameListAccessor;
import ui.data.ServerFacade;
import ui.data.WebSocketClient;
import ui.menu.MenuCommandHandler;
import ui.menu.MenuCommandHandlerFactory;
import util.SerializerFactory;
import java.util.logging.*;

public class Main
{
    private static AppState appState;
    private static MenuCommandHandlerFactory menuCommandFactory;
    private static BufferedRenderer render;
    private static Gson gson;

    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");

        appState = new AppState();
        gson = new SerializerFactory().getGson();
        render = new BufferedRenderer();

        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger("default");

        try (ServerFacade serverFacade = new ServerFacade(gson);
             WebSocketClient webSocketClient = new WebSocketClient(appState, gson, render);
             ConsoleReader consoleReader = new ConsoleReader())
        {
            GameListAccessor gameListAccessor = new GameListAccessor(appState, serverFacade);
            render.using(gameListAccessor);
            menuCommandFactory = new MenuCommandHandlerFactory(appState, logger, render,
                                                               gameListAccessor, serverFacade, webSocketClient);

            logger.addHandler(new FileHandler("chess-app.log", true));
            serverFacade.bindTo("localhost", 8080);
            webSocketClient.connect("localhost", 8080);

            runUsing(consoleReader);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Critical failure", e);
            render.error("A critical error has occurred; the app will have to stop.");
        }
    }

    private static void runUsing(ConsoleReader consoleReader) {
        while (true) {
            String context = appState.currentUsername();
            if(appState.inGameplayMode()) {
                if(appState.isObserving()) {
                    context += " watching ";
                } else {
                    context += " playing ";
                }
                context += appState.gameName();
            }
            render.prompt(context);
            consoleReader.read();

            MenuCommandHandler command;
            if (!appState.userIsLoggedIn()) {
                command = menuCommandFactory.getPreLoginCommand(consoleReader.firstToken());
            } else if(!appState.inGameplayMode()) {
                command = menuCommandFactory.getPostLoginCommand(consoleReader.firstToken());
            } else {
                command = menuCommandFactory.getGameplayCommand(consoleReader.firstToken());
            }

            if (command == null)   //user selected to quit
            {
                System.out.println("Have a great day!");
                System.out.println();
                return;
            }

            String errorMessage = command.execute(consoleReader.allButFirstToken());

            if (errorMessage != null) {
                render.error(errorMessage);
            }
        }
    }
}