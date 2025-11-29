package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import server.TypeFactory;
import service.SessionService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;

import javax.security.auth.login.LoginException;

public class MessageRouter implements WsMessageHandler
{
    private final TypeFactory factory;
    private final Gson gson;
    private final SessionService sessionService;

    public MessageRouter(TypeFactory typeFactory) {
        this.factory = typeFactory;
        this.gson = factory.getGson();
        this.sessionService = factory.getSessionService();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception
    {
        ServerMessage message;

        try
        {
            UserGameCommand clientMessage = gson.fromJson(wsMessageContext.message(), UserGameCommand.class);
            sessionService.validateSession(clientMessage.getAuthToken());

            MessageHandler handler = factory.getMessageHandler(clientMessage.getCommandType());

            if (handler != null) {
                message = handler.handle(clientMessage);
            } else {
                message = new ServerErrorMessage("Invalid Command");
            }
        }
        catch(LoginException ex)
        {
            message = new ServerErrorMessage(ex.getMessage());
        }
        catch(Exception ex)
        {
            //TODO: log
            message = new ServerErrorMessage("A fatal error occurred; the message was not processed.");
        }

        String json = gson.toJson(message);
        wsMessageContext.send(json);
    }
}
