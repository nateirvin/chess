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
        ArrayList<GamePlayContext> gameClients;

        if(!clients.containsKey(gameID)) {
            gameClients = new ArrayList<>();
        } else {
            gameClients = clients.get(gameID);
        }

        gameClients.add(new GamePlayContext(session, callerContext));
        clients.put(gameID, gameClients);
    }

    void unregister(int gameID, String authToken) {
        if(clients.containsKey(gameID)) {
            ArrayList<GamePlayContext> gameClients = clients.get(gameID);
            gameClients.removeIf(c -> c.loginInfo().authToken().equals(authToken));
            clients.put(gameID, gameClients);
        }
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

    void sendToClient(@NotNull WsContext client, ServerMessage message) {
        client.send(gson.toJson(message));
    }

    static void logError(String message) {
        //TODO: better logging
        System.out.println(message);
    }
}
