package ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogoutUserCommandHandler implements MenuCommandHandler {
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;

    public LogoutUserCommandHandler(AppState appState, Logger logger, ServerFacade serverFacade) {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
    }

    @Override
    public String execute(String... arguments)
    {
        try {
            serverFacade.logoutUser(appState.getAuthToken());
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Logout failure", e);
            return "Could not log you out.";
        }

        appState.endSession();

        System.out.println("You have been logged out.");
        System.out.println();

        return null;
    }
}
