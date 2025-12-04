package ui.menu.game;

import chess.ChessMove;
import chess.ChessPosition;
import jakarta.websocket.DeploymentException;
import model.UserEntryResult;
import ui.BufferedRenderer;
import ui.data.AppState;
import ui.data.WebSocketClient;
import ui.menu.GameScopedCommandHandler;
import ui.menu.MenuCommandHandler;
import websocket.commands.UserMoveCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MakeMoveCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler {
    private final Logger logger;
    private final WebSocketClient webSocketClient;

    public MakeMoveCommandHandler(AppState appState, Logger logger, BufferedRenderer renderer, WebSocketClient webSocketClient) {
        super(appState, renderer);
        this.logger = logger;
        this.webSocketClient = webSocketClient;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length != 2) {
            return "Invalid arguments";
        }

        UserEntryResult<ChessPosition> fromResult = getPosition(arguments[0].trim());
        UserEntryResult<ChessPosition> toResult = getPosition(arguments[1].trim());
        if(!fromResult.success()) {
            return fromResult.getErrorMessage();
        }
        if(!toResult.success()) {
            return toResult.getErrorMessage();
        }

        //TODO: implement the promo piece logic
        ChessMove move = new ChessMove(fromResult.getValue(), toResult.getValue(), null);

        if(appState.userIsObserver()) {
            return "You cannot make moves while observing.";
        }

        try {
            webSocketClient.send(new UserMoveCommand(appState.getAuthToken(), appState.getCurrentGame().gameID(), move));
        } catch (DeploymentException ex) {
            logger.log(Level.SEVERE, "Make Move Failed.", ex);
            return "Cannot contact game server.";
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Make Move Failed.", ex);
            return "Could not communicate the move.";
        }

        render.waitForBoard();

        renderGameOverIfDone();
        return null;
    }
}
