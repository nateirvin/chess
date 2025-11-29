package server.websocket;

import com.google.gson.Gson;
import dataaccess.GameDataAccess;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import server.TypeFactory;
import service.SessionService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.GameLoadServerMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class MessageRouter implements WsMessageHandler {

    private final Gson gson;
    private final SessionService sessionService;
    private final GameDataAccess gameDataAccess;

    private final HashMap<Integer, ArrayList<GamePlayContext>> clients;

    public MessageRouter(TypeFactory factory) {
        this.gson = factory.getGson();
        this.sessionService = factory.getSessionService();
        this.gameDataAccess = factory.getGameDataAccess();

        clients = new HashMap<>();
    }

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
                    if(!clients.containsKey(game.gameID())) {
                        ArrayList<GamePlayContext> c = new ArrayList<>();
                        c.add(new GamePlayContext(session, callerContext));
                        clients.put(game.gameID(), c);
                    } else {
                        ArrayList<GamePlayContext> c = clients.get(game.gameID());
                        c.add(new GamePlayContext(session, callerContext));
                        clients.put(game.gameID(), c);
                    }

                    sendToClient(callerContext, new GameLoadServerMessage(game));

                    NotificationMessage message = new NotificationMessage(session.username() + " has joined " + game.gameName());
                    sendToGameUsersExceptCaller(message, callerMessage);
                }
                else if (callerMessage.getCommandType() == UserGameCommand.CommandType.LEAVE)
                {
                    if(clients.containsKey(game.gameID())) {
                        ArrayList<GamePlayContext> c = clients.get(game.gameID());
                        c.removeIf(x -> x.loginInfo().authToken().equals(callerMessage.getAuthToken()));
                        clients.put(game.gameID(), c);
                    }

                    NotificationMessage message = new NotificationMessage(session.username() + " has left " + game.gameName());
                    sendToGameUsersExceptCaller(message, callerMessage);
                }
                else
                {
                    if(!game.hasPlayer(session.username())) {
                        sendToClient(callerContext, new ServerErrorMessage("Observers cannot make moves."));
                        return;
                    }

                    if(game.isOver()) {
                        sendToClient(callerContext, new ServerErrorMessage("The game is over."));
                        return;
                    }

                    if (callerMessage.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE)
                    {
                        if(!game.isThisPlayersTurn(session.username())) {
                            sendToClient(callerContext, new ServerErrorMessage("It is not your turn."));
                            return;
                        }

                        UserMoveCommand specificMessage = gson.fromJson(callerContext.message(), UserMoveCommand.class);

                        game.getGame().makeMove(specificMessage.getMove());
                        this.gameDataAccess.updateGame(game);

                        NotificationMessage message = new NotificationMessage(session.username() + " moved");  //TODO: more detail
                        sendToGameUsersExceptCaller(message, callerMessage);

                        sendToGameUsers(game.gameID(), new GameLoadServerMessage(game));
                    }
                    else if (callerMessage.getCommandType() == UserGameCommand.CommandType.RESIGN)
                    {
                        this.gameDataAccess.concedeGame(callerMessage.getGameID(), session.userId());
                        sendToGameUsers(game.gameID(), new NotificationMessage(session.username() + " has resigned."));
                    }
                }
            }
            else
            {
                ServerErrorMessage message = new ServerErrorMessage("Invalid Game ID");
                sendToClient(callerContext, message);
            }
        }
        catch (LoginException ex)
        {
            sendToClient(callerContext, new ServerErrorMessage("Invalid Auth Token"));
        }
        catch (Exception ex)
        {
            logError(ex.getMessage());
            sendToClient(callerContext, new ServerErrorMessage("A fatal error has occurred."));
        }
    }

    private void sendToGameUsers(int gameID, ServerMessage serverMessage) {
        sendToGameUsers(gameID, serverMessage, ctx -> true);
    }

    private void sendToGameUsersExceptCaller(ServerMessage message, UserGameCommand callerInfo) {
        sendToGameUsers(callerInfo.getGameID(), message, ctx -> !isCallerContext(ctx, callerInfo));
    }

    private static boolean isCallerContext(GamePlayContext context, UserGameCommand caller) {
        return context.loginInfo().authToken().equals(caller.getAuthToken());
    }

    private void sendToGameUsers(int gameID, ServerMessage message, Predicate<GamePlayContext> userFilter) {
        ArrayList<GamePlayContext> clientContexts = clients.get(gameID);
        for (GamePlayContext clientContext : clientContexts) {
            try {
                if(userFilter.test(clientContext)) {
                    if(clientContext.client().session.isOpen()) {
                        sendToClient(clientContext.client(), message);
                    }
                }
            } catch (Exception exception) {
                logError(exception.getMessage());
            }
        }
    }

    private void sendToClient(@NotNull WsContext client, ServerMessage message) {
        client.send(gson.toJson(message));
    }

    private void logError(String message) {
        //TODO: better logging
        System.out.println(message);
    }
}
