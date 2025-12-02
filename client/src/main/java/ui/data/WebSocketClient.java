package ui.data;

import chess.ChessGame;
import com.google.gson.Gson;
import jakarta.websocket.*;
import model.GameData;
import ui.BufferedRenderer;
import websocket.commands.UserGameCommand;
import websocket.messages.GameLoadServerMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient extends Endpoint implements Closeable
{
    private final AppState appState;
    private final Gson gson;
    private final BufferedRenderer render;
    private Session session;

    public WebSocketClient(AppState appState, Gson gson, BufferedRenderer renderer) {
        this.appState = appState;
        this.gson = gson;
        this.render = renderer;
    }

    public void connect(String host, int port) throws URISyntaxException, DeploymentException, IOException
    {
        if(session != null) {
            throw new IllegalStateException();
        }

        URI uri = new URI("ws://%s:%d/ws".formatted(host, port));
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                    GameLoadServerMessage specificMessage = gson.fromJson(message, GameLoadServerMessage.class);

                    GameData game = specificMessage.getGame();
                    ChessGame.TeamColor color = game.getColorForUser(appState.currentUsername());

                    appState.setGame(game, color);
                    render.board(game.getGame().getBoard(), color);
                } else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                    NotificationMessage specificMessage = gson.fromJson(message, NotificationMessage.class);
                    render.update(specificMessage.getMessage());
                } else if(serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                    ServerErrorMessage specificMessage = gson.fromJson(message, ServerErrorMessage.class);
                    render.error(specificMessage.getErrorMessage());
                }
            }
        });
    }

    public void send(UserGameCommand command) throws IOException {
        if(command == null) {
            throw new IllegalArgumentException();
        }
        if(session == null) {
            throw new IllegalStateException();
        }
        String message = gson.toJson(command);
        session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    @Override
    public void close() throws IOException {
        session.close();
        session = null;
    }
}
