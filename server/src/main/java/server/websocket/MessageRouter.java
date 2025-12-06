package server.websocket;

import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDataAccess;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import server.TypeFactory;
import service.GameService;
import service.SessionService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.*;

import javax.security.auth.login.LoginException;

public class MessageRouter implements WsMessageHandler {

    private final Gson gson;
    private final SessionService sessionService;
    private final GameDataAccess gameDataAccess;
    private final GameService gameService;
    private final ClientManager clientManager;

    public MessageRouter(TypeFactory factory) {
        this.gson = factory.getGson();
        this.sessionService = factory.getSessionService();
        this.gameDataAccess = factory.getGameDataAccess();
        this.gameService = factory.getGameService();
        this.clientManager = new ClientManager(gson);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void handleMessage(@NotNull WsMessageContext callerContext) throws Exception
    {
        try
        {
            ClientCommandContext context = getEnrichedContext(callerContext);

            if (context.getGame() != null)
            {
                if (context.getCommandType() == UserGameCommand.CommandType.CONNECT)
                {
                    handleNewClient(context);
                }
                else if (context.getCommandType() == UserGameCommand.CommandType.LEAVE)
                {
                    handleClientDeparture(context);
                }
                else
                {
                    handleGameplay(context) ;
                }
            }
            else
            {
                ServerErrorMessage message = new ServerErrorMessage("Invalid Game ID");
                clientManager.sendToClient(context.getCaller(), message);
            }
        }
        catch (LoginException ex)
        {
            clientManager.sendToClient(callerContext, new ServerErrorMessage("Invalid Auth Token"));
        }
        catch(InvalidMoveException invalidMoveException) {
            clientManager.sendToClient(callerContext, new ServerErrorMessage(invalidMoveException.getMessage()));
        }
        catch (Exception ex)
        {
            //noinspection ThrowablePrintedToSystemOut
            System.out.println(ex);

            clientManager.sendToClient(callerContext,
                                       new ServerErrorMessage(
                                               "There was an unrecoverable error while processing this action."));
        }
    }

    @NotNull
    private ClientCommandContext getEnrichedContext(@NotNull WsMessageContext callerContext) throws LoginException {
        UserGameCommand callerMessage = gson.fromJson(callerContext.message(), UserGameCommand.class);
        AuthData session = sessionService.validateSession(callerMessage.getAuthToken());
        GameData game = gameDataAccess.getGameById(callerMessage.getGameID());
        return new ClientCommandContext(callerContext, callerMessage, session, game);
    }

    private void handleNewClient(ClientCommandContext context) {
        GameData game = context.getGame();
        WsContext caller = context.getCaller();

        clientManager.register(game.gameID(), context.getSession(), caller);

        clientManager.sendToClient(caller, new GameLoadServerMessage(game));

        NotificationMessage message = 
                new NotificationMessage("%s has joined %s".formatted(context.getSession().username(), game.gameName()));
        clientManager.sendToGameUsersExceptCaller(message, context.getCommand());
    }

    private void handleClientDeparture(ClientCommandContext context) {
        GameData game = context.getGame();
        AuthData session = context.getSession();
        
        gameService.leaveGame(game.gameID(), game.getColorForUser(session.username()));

        clientManager.unregister(game.gameID(), session.authToken());

        NotificationMessage message = new NotificationMessage(session.username() + " has left " + game.gameName());
        clientManager.sendToGameUsersExceptCaller(message, context.getCommand());
    }

    private void handleGameplay(ClientCommandContext context) throws InvalidMoveException {
        WsMessageContext caller = context.getCaller();
        GameData game = context.getGame();
        AuthData session = context.getSession();

        if(!game.hasPlayer(session.username())) {
            clientManager.sendToClient(caller, new ServerErrorMessage("Observers cannot make moves."));
            return;
        }

        if(game.isOver()) {
            clientManager.sendToClient(caller, new ServerErrorMessage("The game is over."));
            return;
        }

        if (context.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE)
        {
            if(!game.isThisPlayersTurn(session.username())) {
                clientManager.sendToClient(caller, new ServerErrorMessage("It is not your turn."));
                return;
            }

            handleMove(context);
        }
        else if (context.getCommandType() == UserGameCommand.CommandType.RESIGN)
        {
            handleResignation(context);
        }
    }

    private void handleMove(ClientCommandContext context) throws InvalidMoveException {
        GameData game = context.getGame();

        UserMoveCommand specificMessage = gson.fromJson(context.getCaller().message(), UserMoveCommand.class);
        ChessMove move = specificMessage.getMove();

        ChessPiece piece = game.getGame().getBoard().getPiece(move.getStartPosition());
        game.getGame().makeMove(move);
        this.gameDataAccess.updateGame(game);

        String updateMessage = "%s moved %s from %s".formatted(context.getSession().username(),
                                                               piece,
                                                               move.inStandardNotation());
        NotificationMessage message = new NotificationMessage(updateMessage);
        clientManager.sendToGameUsersExceptCaller(message, context.getCommand());

        clientManager.sendToGameUsers(game.gameID(), new GameLoadServerMessage(game));
    }

    private void handleResignation(ClientCommandContext context) {
        GameData game = context.getGame();
        AuthData session = context.getSession();

        game.concededBy(session.username());
        this.gameDataAccess.updateGame(game);

        clientManager.sendToGameUsers(game.gameID(), new ResignMessage(session.username()));
    }
}
