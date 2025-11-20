package ui.menu;

import ui.*;
import ui.data.AppState;
import ui.data.HttpFailureException;
import ui.data.ServerFacade;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateGameCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;
    private final BufferedRenderer render;

    public CreateGameCommandHandler(AppState appState, Logger logger, ServerFacade serverFacade,
                                    BufferedRenderer render)
    {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
        this.render = render;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length == 1) {
            String gameName = arguments[0];

            try {
                serverFacade.createGame(appState.getAuthToken(), gameName);
            } catch(ConnectException e) {
                logger.log(Level.INFO, "Cannot connect", e);
                return "Game server cannot be reached.";
            } catch (HttpFailureException | InterruptedException | IOException e) {
                logger.log(Level.SEVERE, "Game creation failed", e);
                return "Game creation failed";
            }

            render.userActionComplete("This game has been created!");
            render.gamesList();

            return null;
        } else {
            return "Invalid arguments";
        }
    }
}
