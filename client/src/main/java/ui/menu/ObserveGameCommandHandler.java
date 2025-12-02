package ui.menu;

import jakarta.websocket.DeploymentException;
import model.GameData;
import model.UserEntryResult;
import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.GameListAccessor;
import ui.data.WebSocketClient;
import websocket.commands.UserGameCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObserveGameCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final Logger logger;
    private final GameListAccessor gameListAccessor;
    private final BufferedRenderer render;
    private final WebSocketClient webSocket;

    public ObserveGameCommandHandler(AppState appState, Logger logger, BufferedRenderer render,
                                     GameListAccessor gameListAccessor, WebSocketClient webSocket)
    {
        this.appState = appState;
        this.logger = logger;
        this.gameListAccessor = gameListAccessor;
        this.render = render;
        this.webSocket = webSocket;
    }

    @Override
    public String execute(String... arguments)
    {
        if (arguments.length != 1) {
            return "Invalid arguments";
        }

        UserEntryResult<Integer> gameNumberResult = getGameNumber(arguments[0]);
        if (!gameNumberResult.success()) {
            return gameNumberResult.getErrorMessage();
        }

        GameData game = gameListAccessor.getGameByNumber(gameNumberResult.getValue());
        if(game == null) {
            return "No such game.";
        }

        try {
            UserGameCommand userCommand =
                    new UserGameCommand(UserGameCommand.CommandType.CONNECT, appState.getAuthToken(), game.gameID());
            webSocket.send(userCommand);

            render.userActionComplete("Observation started.");
        } catch(DeploymentException e) {
            logger.log(Level.INFO, "Cannot connect", e);
            return "Game server cannot be reached.";
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Failed to start observation", exception);
            return "Failed to connect to this game.";
        }

        return null;
    }
}
