package ui.menu;

import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.ServerFacade;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogoutUserCommandHandler implements MenuCommandHandler {
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;
    private final BufferedRenderer render;

    public LogoutUserCommandHandler(AppState appState, Logger logger, ServerFacade serverFacade,
                                    BufferedRenderer render)
    {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
        this.render = render;
    }

    @Override
    public String execute(String... arguments)
    {
        try {
            serverFacade.logoutUser(appState.getAuthToken());
        } catch(ConnectException e) {
            logger.log(Level.INFO, "Cannot connect", e);
            return "Game server cannot be reached.";
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Logout failure", e);
            return "Could not log you out.";
        }

        appState.endSession();
        render.userActionComplete("You have been logged out.");

        return null;
    }
}
