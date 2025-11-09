package ui;

import model.SessionData;

public class LoginUserCommandHandler implements MenuCommandHandler {
    private final AppState appState;
    private final ServerFacade serverFacade;

    public LoginUserCommandHandler(AppState appState, ServerFacade serverFacade) {
        this.appState = appState;
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

        SessionData sessionData = serverFacade.loginUser(username, plainTextPassword);
        appState.setSession(sessionData);

        System.out.println("Login successful!");
        System.out.println();

        return null;
    }
}
