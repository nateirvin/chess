package ui.menu;

import model.AuthData;
import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.ServerFacade;
import java.io.IOException;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterUserCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;
    private final BufferedRenderer render;

    public RegisterUserCommandHandler(AppState appState, Logger logger, ServerFacade serverFacade,
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
            render.userActionComplete("You are registered and logged in!");

            return null;
        }
        else
        {
            return "Invalid inputs";
        }
    }
}
