package ui.menu.game;

import chess.ChessMove;
import chess.ChessPiece;
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
        super(appState, renderer, null);
        this.logger = logger;
        this.webSocketClient = webSocketClient;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length != 2) {
            return "Invalid arguments";
        }

        if(appState.userIsObserver()) {
            return "You cannot make moves while observing.";
        }

        UserEntryResult<ChessPosition> fromResult = getPosition(arguments[0].trim());
        UserEntryResult<ChessPosition> toResult = getPosition(arguments[1].trim());
        if(!fromResult.success()) {
            return fromResult.getErrorMessage();
        }
        if(!toResult.success()) {
            return toResult.getErrorMessage();
        }

        ChessPosition startPosition = fromResult.getValue();
        ChessPosition endPosition = toResult.getValue();
        ChessPiece.PieceType promoPieceType = getPromoPieceIfApplicable(startPosition, endPosition);
        ChessMove move = new ChessMove(startPosition, endPosition, promoPieceType);

        try {
            //sends updated game state to everyone
            //sends message to other users
            webSocketClient.send(new UserMoveCommand(appState.getAuthToken(), appState.getCurrentGame().gameID(), move));
        } catch (DeploymentException ex) {
            logger.log(Level.SEVERE, "Make Move Failed.", ex);
            return "Cannot contact game server.";
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Make Move Failed.", ex);
            return "Could not communicate the move.";
        }

        render.waitForBoard();

        if(!displayGameOver()) {
            displayTurnPlayer();
        }

        return null;
    }

    private ChessPiece.PieceType getPromoPieceIfApplicable(ChessPosition startPosition, ChessPosition endPosition)
    {
        ChessPiece piece = appState.getCurrentGame().getGame().getBoard().getPiece(startPosition);

        if(piece != null &&
           piece.getPieceType() == ChessPiece.PieceType.PAWN &&
           piece.getTeamColor() == this.appState.getPlayer() &&
           (endPosition.getRow() == 1 || endPosition.getRow() == 8))
        {
            while(true)
            {
                //noinspection SpellCheckingInspection
                render.promptAndWait("Do you want to promote to (q)ueen, K(n)ight (b)ishop, or (r)ook?");

                String promoPieceEntry = render.firstWordEntered();
                if(promoPieceEntry != null) {
                    promoPieceEntry = promoPieceEntry.trim().toUpperCase();
                    if(promoPieceEntry.startsWith("Q")) {
                        return ChessPiece.PieceType.QUEEN;
                    }
                    else if(promoPieceEntry.startsWith("K")) {
                        return ChessPiece.PieceType.KNIGHT;
                    }
                    else if(promoPieceEntry.startsWith("B")) {
                        return ChessPiece.PieceType.BISHOP;
                    }
                    else if(promoPieceEntry.startsWith("R")) {
                        return ChessPiece.PieceType.ROOK;
                    }
                }
                render.error("Invalid entry");
            }
        }

        return null;
    }
}
