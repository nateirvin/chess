package ui.menu.game;

import jakarta.websocket.DeploymentException;
import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.WebSocketClient;
import ui.menu.GameScopedCommandHandler;
import ui.menu.MenuCommandHandler;
import websocket.commands.UserGameCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResignCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler {
    private final Logger logger;
    private final WebSocketClient webSocket;

    public ResignCommandHandler(AppState appState, Logger logger, BufferedRenderer render, WebSocketClient webSocket) {
        super(appState, render, null);
        this.logger = logger;
        this.webSocket = webSocket;
    }

    @Override
    public String execute(String... arguments) {
        if(appState.userIsObserver()) {
            return "You cannot resign when you are not a player.";
        }

        try {
            UserGameCommand userCommand =
                    new UserGameCommand(UserGameCommand.CommandType.RESIGN,
                                        appState.getAuthToken(),
                                        appState.getCurrentGame().gameID());
            webSocket.send(userCommand);
        } catch (DeploymentException ex) {
            logger.log(Level.SEVERE, "Failed to resign", ex);
            return "Game server cannot be reached";
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to resign", ex);
            return "Failure to communicate";
        }

        render.waitForBoard();

        renderGameOverIfDone();
        return null;
    }
}
