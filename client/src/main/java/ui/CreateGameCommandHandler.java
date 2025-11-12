package ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateGameCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;
    private final GameListDisplay displayer;

    public CreateGameCommandHandler(AppState appState, Logger logger, ServerFacade serverFacade, GameListDisplay displayer) {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
        this.displayer = displayer;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length == 1) {
            String gameName = arguments[0];

            try {
                serverFacade.createGame(appState.getAuthToken(), gameName);
            } catch (HttpFailureException | InterruptedException | IOException e) {
                logger.log(Level.SEVERE, "Game creation failed", e);
                return "Game creation failed";
            }

            System.out.println("This game has been created!");
            System.out.println();
            displayer.showGamesList();

            return null;
        } else {
            return "Invalid arguments";
        }
    }
}
