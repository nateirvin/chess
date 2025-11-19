package ui.menu;

import model.AuthData;
import ui.AppState;
import ui.ServerFacade;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterUserCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;

    public RegisterUserCommandHandler(AppState appState, Logger logger, ServerFacade serverFacade) {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
    }

    @Override
    public String execute(String... arguments)
    {
        if(arguments.length == 2 || arguments.length == 3)
        {
            String userName = arguments[0];
            String plainTextPassword = arguments[1];
            String email = null;
            if(arguments.length == 3) {
                email = arguments[2];
            }

            if(userName.equalsIgnoreCase(AppState.GUEST_USERNAME)) {
                return "This username is reserved and cannot be used.";
            }

            AuthData registrationResult;
            try {
                registrationResult = serverFacade.registerUser(userName, plainTextPassword, email);
            } catch(ConnectException e) {
                logger.log(Level.INFO, "Cannot connect", e);
                return "Game server cannot be reached.";
            } catch (IOException | InterruptedException e) {
                logger.log(Level.SEVERE, "Error on registration endpoint", e);
                return "Registration failed.";
            }
            if(registrationResult == null) {
                return "That username is already in use.";
            }

            appState.setSession(registrationResult);

            System.out.println("You are registered and logged in!");
            System.out.println();

            return null;
        }
        else
        {
            return "Invalid inputs";
        }
    }
}
