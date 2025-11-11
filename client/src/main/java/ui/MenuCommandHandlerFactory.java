package ui;

import java.util.logging.Logger;

public class MenuCommandHandlerFactory
{
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;
    private final GameListDisplay displayer;

    public MenuCommandHandlerFactory(AppState appState, Logger logger, ServerFacade serverFacade, GameListDisplay displayer)
    {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
        this.displayer = displayer;
    }

    public MenuCommandHandler getPreLoginCommand(String commandName)
    {
        return getMenuCommand(commandName, false);
    }

    public MenuCommandHandler getPostLoginCommand(String commandName)
    {
        return getMenuCommand(commandName, true);
    }

    private MenuCommandHandler getMenuCommand(String commandName, boolean isSecured)
    {
        if(commandName == null)
        {
            commandName = "";
        }

        switch (commandName.toLowerCase()) {
            case "register" -> {
                if (!isSecured) {
                    return new RegisterUserCommandHandler(appState, logger, serverFacade);
                }
            }
            case "login" -> {
                if (!isSecured) {
                    return new LoginUserCommandHandler(appState, serverFacade);
                }
            }
            case "logout" -> {
                if(isSecured) {
                    return new LogoutUserCommandHandler(appState, serverFacade);
                }
            }
            case "create" -> {
                if(isSecured) {
                    return new CreateGameCommandHandler(serverFacade, displayer);
                }
            }
            case "list" -> {
                if(isSecured) {
                    return new ListGameCommandHandler(displayer);
                }
            }
            case "observe" -> {
                if(isSecured) {
                    return new ObserveGameCommandHandler(displayer);
                }
            }
            case "join" -> {
                if(isSecured) {
                    return new JoinGameCommandHandler(appState, serverFacade, displayer);
                }
            }
            case "help" -> {
                if(isSecured) {
                    return new PostloginHelpCommandHandler();
                }
                return new PreloginHelpCommandHandler();
            }
            case "quit", "exit" -> {
                return null;
            }
        }

        return new InvalidMenuCommandHandler();
    }
}
