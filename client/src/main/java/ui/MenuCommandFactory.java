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
        if(commandName == null)
        {
            commandName = "";
        }

        switch (commandName.toLowerCase()) {
            case "register" -> {
                return new RegisterUserCommand(appState, serverFacade);
            }
            case "login" -> {
                return new LoginUserCommand();
            }
            case "help" -> {
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

    public MenuCommand getPostloginCommand(String commandName)
    {
        switch (commandName.toLowerCase()) {
            case "help" -> {
                return new PostloginHelpCommand();
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
