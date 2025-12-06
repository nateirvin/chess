package ui.data;

import com.google.gson.Gson;
import jakarta.websocket.*;
import model.GameData;
import ui.BufferedRenderer;
import ui.menu.GameScopedCommandHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient extends Endpoint implements Closeable
{
    private String host;
    private int port;
    private Session session;

    private final AppState appState;
    private final Gson gson;
    private final BufferedRenderer render;

    public WebSocketClient(AppState appState, Gson gson, BufferedRenderer renderer) {
        this.appState = appState;
        this.gson = gson;
        this.render = renderer;
    }

    public void bindTo(String host, int port)  {
        if(session != null) {
            throw new IllegalStateException();
        }

        this.host = host;
        this.port = port;
    }

    private void ensureConnection() throws DeploymentException, IOException {
        if(session != null) {
            return;
        }

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, getUri());

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                handleCallbacks(message);
            }
        });
    }

    private URI getUri() {
        try {
            return new URI("ws://%s:%d/ws".formatted(host, port));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleCallbacks(String rawMessage) {
        ServerMessage serverMessage = gson.fromJson(rawMessage, ServerMessage.class);
        if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            handleGameLoad(rawMessage);
        } else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            handleGameActivity(rawMessage);
        } else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
            ServerErrorMessage errorMessage = gson.fromJson(rawMessage, ServerErrorMessage.class);
            render.callbackError(errorMessage.getErrorMessage());
        }
    }

    private void handleGameLoad(String rawMessage) {
        GameLoadServerMessage serverMessage = gson.fromJson(rawMessage, GameLoadServerMessage.class);
        GameData game = serverMessage.getGame();

        appState.updateGame(game);
        render.updateBoard(game.getGame().getBoard(), appState.getPlayer());

        GameScopedCommandHandler.displayTurnPlayer(appState, render);
    }

    private void handleGameActivity(String rawMessage) {
        ResignMessage moreSpecificMessage = gson.fromJson(rawMessage, ResignMessage.class);

        //if the resignation was initiated by this user, no need to update anything
        if(!moreSpecificMessage.isEmpty()) {
            if(appState.currentUsername().equals(moreSpecificMessage.getUsername())) {
                return;
            }
        }

        NotificationMessage serverMessage = gson.fromJson(rawMessage, NotificationMessage.class);
        render.asyncUpdate(serverMessage.getMessage());
    }

    public void send(UserGameCommand command) throws IOException, DeploymentException {
        if(command == null) {
            throw new IllegalArgumentException();
        }

        ensureConnection();

        String message = gson.toJson(command);
        session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    @Override
    public void close() throws IOException {
        if(session != null) {
            session.close();
        }

        session = null;
    }
}
