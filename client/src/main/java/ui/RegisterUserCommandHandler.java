package ui;

import model.SessionData;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterUserCommandHandler implements MenuCommandHandler
{
    public static final String GUEST_USERNAME = "guest";

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

            if(userName.equalsIgnoreCase(GUEST_USERNAME)) {
                return "This username is reserved and cannot be used.";
            }

            SessionData registrationResult;
            try {
                registrationResult = serverFacade.registerUser(userName, plainTextPassword, email);
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
