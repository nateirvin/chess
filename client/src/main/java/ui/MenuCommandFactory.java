package ui;

public class MenuCommandFactory
{
    private final AppState appState;
    private final ServerFacade serverFacade;
    private final GameListDisplay displayer;

    public MenuCommandFactory(AppState appState, ServerFacade serverFacade, GameListDisplay displayer)
    {
        this.appState = appState;
        this.serverFacade = serverFacade;
        this.displayer = displayer;
    }

    public MenuCommand getPreLoginCommand(String commandName)
    {
        return getMenuCommand(commandName, false);
    }

    public MenuCommand getPostLoginCommand(String commandName)
    {
        return getMenuCommand(commandName, true);
    }

    private MenuCommand getMenuCommand(String commandName, boolean isSecured)
    {
        if(commandName == null)
        {
            commandName = "";
        }

        switch (commandName.toLowerCase()) {
            case "register" -> {
                if (!isSecured) {
                    return new RegisterUserCommand(appState, serverFacade);
                }
            }
            case "login" -> {
                if (!isSecured) {
                    return new LoginUserCommand(appState, serverFacade);
                }
            }
            case "logout" -> {
                if(isSecured) {
                    return new LogoutUserCommand(appState, serverFacade);
                }
            }
            case "create" -> {
                if(isSecured) {
                    return new CreateGameCommand(serverFacade, displayer);
                }
            }
            case "list" -> {
                if(isSecured) {
                    return new ListGameCommand(displayer);
                }
            }
            case "join" -> {
                if(isSecured) {
                    return new JoinGameCommand(appState, serverFacade, displayer);
                }
            }
            case "help" -> {
                if(isSecured) {
                    return new PostloginHelpCommand();
                }
                return new PreloginHelpCommand();
            }
            case "quit", "exit" -> {
                return null;
            }
        }

        return new InvalidMenuCommand();
    }
}
