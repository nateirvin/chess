package server.websocket;

import com.google.gson.Gson;
import dataaccess.GameDataAccess;
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
import websocket.messages.GameLoadServerMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;
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
            UserGameCommand callerMessage = gson.fromJson(callerContext.message(), UserGameCommand.class);

            AuthData session = sessionService.validateSession(callerMessage.getAuthToken());
            GameData game = gameDataAccess.getGameById(callerMessage.getGameID());

            if (game != null)
            {
                if (callerMessage.getCommandType() == UserGameCommand.CommandType.CONNECT)
                {
                    clientManager.register(game.gameID(), session, callerContext);

                    clientManager.sendToClient(callerContext, new GameLoadServerMessage(game));

                    NotificationMessage message = new NotificationMessage(session.username() + " has joined " + game.gameName());
                    clientManager.sendToGameUsersExceptCaller(message, callerMessage);
                }
                else if (callerMessage.getCommandType() == UserGameCommand.CommandType.LEAVE)
                {
                    gameService.leaveGame(game.gameID(), game.getColorForUser(session.username()));

                    clientManager.unregister(game.gameID(), callerMessage.getAuthToken());

                    NotificationMessage message = new NotificationMessage(session.username() + " has left " + game.gameName());
                    clientManager.sendToGameUsersExceptCaller(message, callerMessage);
                }
                else
                {
                    if(!game.hasPlayer(session.username())) {
                        clientManager.sendToClient(callerContext, new ServerErrorMessage("Observers cannot make moves."));
                        return;
                    }

                    if(game.isOver()) {
                        clientManager.sendToClient(callerContext, new ServerErrorMessage("The game is over."));
                        return;
                    }

                    if (callerMessage.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE)
                    {
                        if(!game.isThisPlayersTurn(session.username())) {
                            clientManager.sendToClient(callerContext, new ServerErrorMessage("It is not your turn."));
                            return;
                        }

                        UserMoveCommand specificMessage = gson.fromJson(callerContext.message(), UserMoveCommand.class);

                        game.getGame().makeMove(specificMessage.getMove());
                        this.gameDataAccess.updateGame(game);

                        NotificationMessage message = new NotificationMessage(session.username() + " moved");  //TODO: more detail
                        clientManager.sendToGameUsersExceptCaller(message, callerMessage);

                        clientManager.sendToGameUsers(game.gameID(), new GameLoadServerMessage(game));
                    }
                    else if (callerMessage.getCommandType() == UserGameCommand.CommandType.RESIGN)
                    {
                        this.gameDataAccess.concedeGame(callerMessage.getGameID(), session.userId());
                        clientManager.sendToGameUsers(game.gameID(), new NotificationMessage(session.username() + " has resigned."));
                    }
                }
            }
            else
            {
                ServerErrorMessage message = new ServerErrorMessage("Invalid Game ID");
                clientManager.sendToClient(callerContext, message);
            }
        }
        catch (LoginException ex)
        {
            clientManager.sendToClient(callerContext, new ServerErrorMessage("Invalid Auth Token"));
        }
        catch (Exception ex)
        {
            ClientManager.logError(ex.getMessage());  //TODO: better encapsulation
            clientManager.sendToClient(callerContext, new ServerErrorMessage("A fatal error has occurred."));
        }
    }
}
