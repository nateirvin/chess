package ui;

public class LogoutUserCommandHandler implements MenuCommandHandler {
    private final AppState appState;
    private final ServerFacade serverFacade;

    public LogoutUserCommandHandler(AppState appState, ServerFacade serverFacade) {
        this.appState = appState;
        this.serverFacade = serverFacade;
    }

    @Override
    public String execute(String... arguments) {
        serverFacade.logoutUser(appState.getAuthToken());
        appState.endSession();

        System.out.println("You have been logged out.");
        System.out.println();

        return null;
    }
}
