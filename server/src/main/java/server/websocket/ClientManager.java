package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

class ClientManager
{
    private final Gson gson;
    private final HashMap<Integer, ArrayList<GamePlayContext>> clients;

    public ClientManager(Gson gson)
    {
        this.gson = gson;
        clients = new HashMap<>();
    }

    void register(int gameID, AuthData session, WsContext callerContext) {
        synchronized (clients) {
            ArrayList<GamePlayContext> gameClients = getClientsFor(gameID);
            gameClients.add(new GamePlayContext(session, callerContext));
            clients.put(gameID, gameClients);
        }
    }

    private ArrayList<GamePlayContext> getClientsFor(int gameID) {
        if(!clients.containsKey(gameID)) {
            return new ArrayList<>();
        } else {
            return clients.get(gameID);
        }
    }

    void unregister(int gameID, String authToken) {
        synchronized (clients) {
            if(clients.containsKey(gameID)) {
                removeClient(gameID, authToken);
            }
        }
    }

    private void removeClient(int gameID, String authToken) {
        ArrayList<GamePlayContext> gameClients = clients.get(gameID);
        gameClients.removeIf(ctx -> ctx.loginInfo().authToken().equals(authToken));
        clients.put(gameID, gameClients);
    }

    void sendToGameUsers(int gameID, ServerMessage serverMessage) {
        sendToGameUsers(gameID, serverMessage, ctx -> true);
    }

    void sendToGameUsersExceptCaller(ServerMessage message, UserGameCommand callerInfo) {
        sendToGameUsers(callerInfo.getGameID(), message, ctx -> !isCallerContext(ctx, callerInfo));
    }

    private static boolean isCallerContext(GamePlayContext context, UserGameCommand caller) {
        return context.loginInfo().authToken().equals(caller.getAuthToken());
    }

    private void sendToGameUsers(int gameID, ServerMessage message, Predicate<GamePlayContext> userFilter) {
        synchronized (clients) {
            ArrayList<GamePlayContext> clientContexts = clients.get(gameID);
            for (GamePlayContext clientContext : clientContexts) {
                try {
                    if(userFilter.test(clientContext)) {
                        sendToClient(clientContext, message);
                    }
                } catch (Exception exception) {
                    System.out.println(exception);
                }
            }
        }
    }

    private void sendToClient(GamePlayContext clientContext, ServerMessage message) {
        if(clientContext.client().session.isOpen()) {
            sendToClient(clientContext.client(), message);
        }
    }

    void sendToClient(@NotNull WsContext client, ServerMessage message) {
        client.send(gson.toJson(message));
    }
}
