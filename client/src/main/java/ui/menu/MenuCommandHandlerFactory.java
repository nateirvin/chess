package ui.menu;

import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.GameListAccessor;
import ui.data.ServerFacade;
import ui.menu.game.*;
import ui.menu.help.*;
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
        if(commandName == null)
        {
            commandName = "";
        }

        switch (commandName.toLowerCase()) {
            case "register" -> {
                return new RegisterUserCommandHandler(appState, logger, serverFacade, mainRenderer);
            }
            case "login" -> {
                return new LoginUserCommandHandler(appState, logger, serverFacade, mainRenderer);
            }
            case "help" -> {
                return new PreloginHelpCommandHandler(mainRenderer);
            }
            case "quit", "exit" -> {
                return null;
            }
        }

        return new InvalidMenuCommandHandler(mainRenderer);
    }

    public MenuCommandHandler getPostLoginCommand(String commandName)
    {
        if(commandName == null)
        {
            commandName = "";
        }

        switch (commandName.toLowerCase()) {
            case "logout" -> {
                return new LogoutUserCommandHandler(appState, logger, serverFacade, mainRenderer);
            }
            case "create" -> {
                return new CreateGameCommandHandler(appState, logger, serverFacade, mainRenderer);
            }
            case "list" -> {
                return new ListGameCommandHandler(logger, mainRenderer);
            }
            case "observe" -> {
                return new ObserveGameCommandHandler(appState, gameListAccessor, mainRenderer);
            }
            case "join" -> {
                return new JoinGameCommandHandler(appState, logger, serverFacade, gameListAccessor, mainRenderer);
            }
            case "help" -> {
                return new PostloginHelpCommandHandler(mainRenderer);
            }
            case "quit", "exit" -> {
                return null;
            }
        }

        return new InvalidMenuCommandHandler(mainRenderer);
    }

    public MenuCommandHandler getGameplayCommand(String commandName)
    {
        if(commandName == null)
        {
            commandName = "";
        }

        switch (commandName.toLowerCase()) {
            case "redraw" -> {
                return new RedrawCommandHandler(appState, mainRenderer);
            }
            case "leave" -> {
                return new LeaveGameCommandHandler(appState, mainRenderer);
            }
            case "moves" -> {
                return new HighlightMovesCommandHandler();
            }
            case "move" -> {
                return new MakeMoveCommandHandler();
            }
            case "resign" -> {
                return new ResignCommandHandler();
            }
            case "help" -> {
                return new GameplayHelpCommandHandler(mainRenderer);
            }
            case "quit", "exit" -> {
                return null;
            }
        }

        return new InvalidMenuCommandHandler(mainRenderer);
    }
}
