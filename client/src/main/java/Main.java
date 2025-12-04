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
    private static Gson gson;
    private static MenuCommandHandlerFactory menuCommandFactory;

    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");

        appState = new AppState();
        gson = new SerializerFactory().getGson();

        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger("default");

        try (BufferedRenderer render = new BufferedRenderer();
             ServerFacade serverFacade = new ServerFacade(gson);
             WebSocketClient webSocketClient = new WebSocketClient(appState, gson, render))
        {
            GameListAccessor gameListAccessor = new GameListAccessor(appState, serverFacade);
            render.using(gameListAccessor);
            menuCommandFactory = new MenuCommandHandlerFactory(appState, logger, render,
                                                               gameListAccessor, serverFacade, webSocketClient);

            logger.addHandler(new FileHandler("chess-app.log", true));
            serverFacade.bindTo("localhost", 8080);
            webSocketClient.bindTo("localhost", 8080);

            runUsing(render);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Critical failure", e);
            System.out.println("A critical error has occurred; the app will have to stop.");
        }
    }

    private static void runUsing(BufferedRenderer screen) {
        while (true) {
            String context = appState.currentUsername();
            if(appState.inGameplayMode()) {
                if(appState.userIsObserver()) {
                    context += " watching ";
                } else {
                    context += " playing ";
                }
                context += appState.gameName();
            }
            screen.promptAndWait(context);

            MenuCommandHandler command;
            if (!appState.userIsLoggedIn()) {
                command = menuCommandFactory.getPreLoginCommand(screen.firstWordEntered());
            } else if(!appState.inGameplayMode()) {
                command = menuCommandFactory.getPostLoginCommand(screen.firstWordEntered());
            } else {
                command = menuCommandFactory.getGameplayCommand(screen.firstWordEntered());
            }

            if (command == null)   //user selected to quit
            {
                System.out.println("Have a great day!");
                System.out.println();
                return;
            }

            String errorMessage = command.execute(screen.allButFirstEnteredWord());

            if (errorMessage != null) {
                screen.error(errorMessage);
            }
        }
    }
}