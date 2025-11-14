package ui;

import model.SessionData;

import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginUserCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;

    public LoginUserCommandHandler(AppState appState, Logger logger, ServerFacade serverFacade) {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
    }

    @Override
    public String execute(String... arguments) {
        if(appState.userIsLoggedIn()) {
            return "You are already logged in.";
        }

        String username;
        String plainTextPassword;
        if(arguments.length == 2) {
            username = arguments[0];
            plainTextPassword = arguments[1];
        } else {
            return "Invalid arguments";
        }

        SessionData sessionData;
        try {
            sessionData = serverFacade.loginUser(username, plainTextPassword);
        } catch(ConnectException e) {
            logger.log(Level.INFO, "Cannot connect", e);
            return "Game server cannot be reached.";
        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Login call failed", e);
            return "There was an error during login.";
        }
        if(sessionData == null) {
            return "Incorrect username or password.";
        }

        appState.setSession(sessionData);

        System.out.println("Login successful!");
        System.out.println();

        return null;
    }
}
