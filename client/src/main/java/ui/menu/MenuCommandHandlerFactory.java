package ui.menu;

import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.GameListAccessor;
import ui.data.ServerFacade;
import java.util.logging.Logger;

public class MenuCommandHandlerFactory
{
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;
    private final GameListAccessor gameListAccessor;
    private final BufferedRenderer mainRenderer;

    public MenuCommandHandlerFactory(AppState appState, Logger logger, ServerFacade serverFacade,
                                     GameListAccessor gameListAccessor, BufferedRenderer mainRenderer)
    {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
        this.gameListAccessor = gameListAccessor;
        this.mainRenderer = mainRenderer;
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
                    return new RegisterUserCommandHandler(appState, logger, serverFacade, mainRenderer);
                }
            }
            case "login" -> {
                if (!isSecured) {
                    return new LoginUserCommandHandler(appState, logger, serverFacade, mainRenderer);
                }
            }
            case "logout" -> {
                if(isSecured) {
                    return new LogoutUserCommandHandler(appState, logger, serverFacade, mainRenderer);
                }
            }
            case "create" -> {
                if(isSecured) {
                    return new CreateGameCommandHandler(appState, logger, serverFacade, mainRenderer);
                }
            }
            case "list" -> {
                if(isSecured) {
                    return new ListGameCommandHandler(logger, mainRenderer);
                }
            }
            case "observe" -> {
                if(isSecured) {
                    return new ObserveGameCommandHandler(gameListAccessor, mainRenderer);
                }
            }
            case "join" -> {
                if(isSecured) {
                    return new JoinGameCommandHandler(appState, logger, serverFacade, gameListAccessor, mainRenderer);
                }
            }
            case "help" -> {
                if(isSecured) {
                    return new PostloginHelpCommandHandler(mainRenderer);
                }
                return new PreloginHelpCommandHandler(mainRenderer);
            }
            case "quit", "exit" -> {
                return null;
            }
        }

        return new InvalidMenuCommandHandler(mainRenderer);
    }
}
