import ui.*;
import ui.data.AppState;
import ui.menu.MenuCommandHandler;
import java.util.UUID;
import java.util.logging.*;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");

        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger("default");

        try (BufferedRenderer render = new BufferedRenderer(new ConsoleReader(), new ConsoleWriter());
             Application app = new Application(logger, render))
        {
            String pattern = "client%s.log".formatted(UUID.randomUUID().toString());
            logger.addHandler(new FileHandler(pattern, true));

            app.bindToHost("localhost", 8080);

            runUsing(app);
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Critical failure", e);
            System.out.println("A critical error has occurred; the app will have to stop.");
        }
    }

    private static void runUsing(Application app) {
        BufferedRenderer screen = app.getRenderer();

        while (true) {
            String prompt = getPromptFor(app);
            screen.promptAndWait(prompt);

            MenuCommandHandler command = app.getCommand(screen.firstWordEntered());

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

    private static String getPromptFor(Application app) {
        AppState appState = app.getStateManager();

        StringBuilder context = new StringBuilder();
        context.append(appState.currentUsername());
        if(appState.inGameplayMode()) {
            context.append(" ");
            if(appState.userIsObserver()) {
                context.append("watching");
            } else {
                context.append("playing");
            }
            context.append(" ");
            context.append(appState.gameName());
        }

        return "CHESS [%s] $".formatted(context);
    }
}