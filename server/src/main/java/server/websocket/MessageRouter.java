package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import server.TypeFactory;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;

public class MessageRouter implements WsMessageHandler
{
    private final TypeFactory factory;
    private final Gson gson;

    public MessageRouter(TypeFactory typeFactory) {
        this.factory = typeFactory;
        this.gson = factory.getGson();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception
    {
        UserGameCommand clientMessage = gson.fromJson(wsMessageContext.message(), UserGameCommand.class);

        MessageHandler handler = factory.getMessageHandler(clientMessage.getCommandType());

        ServerMessage message;
        if (handler != null) {
            message = handler.handle(clientMessage);
        } else {
            message = new ServerErrorMessage("Invalid Command");
        }

        String json = gson.toJson(message);
        wsMessageContext.send(json);
    }
}
