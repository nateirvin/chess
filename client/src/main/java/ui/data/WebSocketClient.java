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

        URI uri;
        try {
            uri = new URI("ws://%s:%d/ws".formatted(host, port));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                    GameLoadServerMessage specificMessage = gson.fromJson(message, GameLoadServerMessage.class);
                    GameData game = specificMessage.getGame();

                    appState.updateGame(game);
                    render.updateBoard(game.getGame().getBoard(), appState.getPlayer());

                    GameScopedCommandHandler.displayTurnPlayer(appState, render);
                } else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                    NotificationMessage specificMessage = gson.fromJson(message, NotificationMessage.class);

                    //if the resignation was initiated by this user, no need to update anything
                    if(specificMessage.isResignation()) {
                        ResignMessage moreSpecificMessage = gson.fromJson(message, ResignMessage.class);
                        if(appState.currentUsername().equals(moreSpecificMessage.getUsername())) {
                           return;
                        }
                    }

                    render.asyncUpdate(specificMessage.getMessage());
                } else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                    ServerErrorMessage specificMessage = gson.fromJson(message, ServerErrorMessage.class);
                    render.callbackError(specificMessage.getErrorMessage());
                }
            }
        });
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
