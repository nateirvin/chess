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
            GameData gameData = gameDataAccess.getGameById(callerMessage.getGameID());

            if (gameData != null)
            {
                if (callerMessage.getCommandType() == UserGameCommand.CommandType.CONNECT)
                {
                    if(!clients.containsKey(gameData.gameID())) {
                        ArrayList<GamePlayContext> c = new ArrayList<>();
                        c.add(new GamePlayContext(session, callerContext));
                        clients.put(gameData.gameID(), c);
                    } else {
                        ArrayList<GamePlayContext> c = clients.get(gameData.gameID());
                        c.add(new GamePlayContext(session, callerContext));
                        clients.put(gameData.gameID(), c);
                    }

                    sendToClient(callerContext, new GameLoadServerMessage(gameData));

                    NotificationMessage message = new NotificationMessage(session.username() + " has joined " + gameData.gameName());
                    sendToGameUsersExceptCaller(message, callerMessage);
                }
                else if (callerMessage.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE)
                {
                    UserMoveCommand specificMessage = gson.fromJson(callerContext.message(), UserMoveCommand.class);

                    gameData.getGame().makeMove(specificMessage.getMove());
                    //TODO: save game

                    NotificationMessage message = new NotificationMessage(session.username() + " moved");  //TODO: more detail
                    sendToGameUsersExceptCaller(message, callerMessage);

                    sendForGameUsers(gameData.gameID(), new GameLoadServerMessage(gameData));
                }
                else if (callerMessage.getCommandType() == UserGameCommand.CommandType.LEAVE)
                {
                    if(clients.containsKey(gameData.gameID())) {
                        ArrayList<GamePlayContext> c = clients.get(gameData.gameID());
                        c.removeIf(x -> x.loginInfo().authToken().equals(callerMessage.getAuthToken()));
                        clients.put(gameData.gameID(), c);
                    }

                    NotificationMessage message = new NotificationMessage(session.username() + " has left " + gameData.gameName());
                    sendToGameUsersExceptCaller(message, callerMessage);
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

    private void sendForGameUsers(int gameID, ServerMessage serverMessage) {
        sendToGameUsers(gameID, serverMessage, ctx -> true);
    }

    private void sendToGameUsersExceptCaller(ServerMessage message, UserGameCommand callerInfo) {
        sendToGameUsers(callerInfo.getGameID(), message, ctx -> !isCallerContext(ctx, callerInfo));
    }

    private static boolean isCallerContext(GamePlayContext context, UserGameCommand caller) {
        return context.loginInfo().authToken().equals(caller.getAuthToken());
    }

    private void sendToGameUsers(Integer gameID, ServerMessage message, Predicate<GamePlayContext> userFilter) {
        ArrayList<GamePlayContext> clientContexts = clients.get(gameID);
        for (GamePlayContext clientContext : clientContexts)
        {
            try
            {
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
