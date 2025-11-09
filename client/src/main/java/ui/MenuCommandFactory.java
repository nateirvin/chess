package ui;

public class MenuCommandFactory
{
    private final AppState appState;
    private final ServerFacade serverFacade;

    public MenuCommandFactory(AppState appState, ServerFacade serverFacade)
    {
        this.appState = appState;
        this.serverFacade = serverFacade;
    }

    public MenuCommand getPreloginCommand(String commandName)
    {
        return getMenuCommand(commandName, false);
    }

    public MenuCommand getPostloginCommand(String commandName)
    {
        return getMenuCommand(commandName, true);
    }

    private MenuCommand getMenuCommand(String commandName, boolean wantsSecuredContent)
    {
        if(commandName == null)
        {
            commandName = "";
        }

        switch (commandName.toLowerCase()) {
            case "register" -> {
                if(wantsSecuredContent) {
                    return new InvalidMenuCommand();
                }
                return new RegisterUserCommand(appState, serverFacade);
            }
            case "login" -> {
                if(wantsSecuredContent) {
                    return new InvalidMenuCommand();
                }
                return new LoginUserCommand(appState, serverFacade);
            }
            case "logout" -> {
                if(wantsSecuredContent) {
                    return new LogoutUserCommand(appState, serverFacade);
                }
                return new InvalidMenuCommand();
            }
            case "help" -> {
                if(wantsSecuredContent) {
                    return new PostloginHelpCommand();
                }
                return new PreloginHelpCommand();
            }
            case "quit", "exit" -> {
                return null;
            }
            default -> {
                return new InvalidMenuCommand();
            }
        }
    }
}
