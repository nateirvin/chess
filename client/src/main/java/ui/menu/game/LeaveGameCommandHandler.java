package ui.menu.game;

import jakarta.websocket.DeploymentException;
import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.WebSocketClient;
import ui.menu.MenuCommandHandler;
import websocket.commands.UserGameCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LeaveGameCommandHandler implements MenuCommandHandler {
    private final AppState appState;
    private final Logger logger;
    private final BufferedRenderer render;
    private final WebSocketClient webSocketClient;

    public LeaveGameCommandHandler(AppState appState, Logger logger, BufferedRenderer render, WebSocketClient webSocketClient) {
        this.appState = appState;
        this.logger = logger;
        this.render = render;
        this.webSocketClient = webSocketClient;
    }

    @Override
    public String execute(String... arguments) {
        boolean wasPlayer = !appState.userIsObserver();

        UserGameCommand command =
                new UserGameCommand(UserGameCommand.CommandType.LEAVE,
                                    appState.getAuthToken(),
                                    appState.getCurrentGame().gameID());

        try {
            //no callback, informs other users
            webSocketClient.send(command);
        } catch (DeploymentException e) {
            logger.log(Level.SEVERE, "Failure to exit game", e);
            return "Game server not reachable.";
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failure to exit game", e);
            return "Could not leave the game.";
        }

        appState.unsetGame();

        render.userActionComplete(wasPlayer ? "Thanks for playing!" : "You are no longer watching this game.");
        return null;
    }
}
