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
import websocket.messages.GameLoadServerMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;
import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class MessageRouter implements WsMessageHandler {

    private final Gson gson;
    private final SessionService sessionService;
    private final GameDataAccess gameDataAccess;

    private final HashMap<String, WsContext> clients;

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

            if (callerMessage.getCommandType() == UserGameCommand.CommandType.CONNECT)
            {
                clients.put(session.authToken(), callerContext);

                GameData gameData = gameDataAccess.getGameById(callerMessage.getGameID());

                if (gameData != null)
                {
                    sendToClient(callerContext, new GameLoadServerMessage(gameData));

                    NotificationMessage message = new NotificationMessage(session.username() + " has joined " + gameData.gameName());
                    sendToAllButCaller(message, session.authToken());
                }
                else
                {
                    ServerErrorMessage message = new ServerErrorMessage("Invalid Game ID");
                    sendToClient(callerContext, message);
                }
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

    private void sendToAllButCaller(NotificationMessage message, String callerUserName) {
        for (var client : clients.entrySet()) {
            String clientAuthToken = client.getKey();
            WsContext clientContext = client.getValue();

            if (clientContext.session.isOpen()) {
                if (!clientAuthToken.equals(callerUserName)) {
                    try {
                        sendToClient(clientContext, message);
                    } catch (Exception exception) {
                        logError(exception.getMessage());
                    }
                }
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
