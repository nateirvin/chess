package ui.menu;

import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.GameListAccessor;
import ui.data.ServerFacade;
import ui.data.WebSocketClient;
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
    private final WebSocketClient webSocketClient;

    public MenuCommandHandlerFactory(AppState appState, Logger logger, BufferedRenderer mainRenderer, GameListAccessor gameListAccessor, ServerFacade serverFacade,
                                     WebSocketClient webSocketClient)
    {
        this.appState = appState;
        this.logger = logger;
        this.serverFacade = serverFacade;
        this.gameListAccessor = gameListAccessor;
        this.mainRenderer = mainRenderer;
        this.webSocketClient = webSocketClient;
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
            case "list", "ls", "games" -> {
                return new ListGameCommandHandler(logger, mainRenderer, gameListAccessor);
            }
            case "observe" -> {
                return new ObserveGameCommandHandler(appState, logger, mainRenderer, gameListAccessor, webSocketClient);
            }
            case "join" -> {
                return new JoinGameCommandHandler(appState, mainRenderer, logger,
                                                  gameListAccessor, serverFacade, webSocketClient);
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
                return new LeaveGameCommandHandler(appState, logger, mainRenderer, webSocketClient);
            }
            case "moves" -> {
                return new HighlightMovesCommandHandler(appState, mainRenderer);
            }
            case "move" -> {
                return new MakeMoveCommandHandler(appState, logger, mainRenderer, webSocketClient);
            }
            case "resign" -> {
                return new ResignCommandHandler(appState, logger, mainRenderer, webSocketClient);
            }
            case "help" -> {
                return new GameplayHelpCommandHandler(appState, mainRenderer);
            }
            case "quit", "exit" -> {
                return null;
            }
        }

        return new InvalidMenuCommandHandler(mainRenderer);
    }
}
