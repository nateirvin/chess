package server;

import io.javalin.*;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.GameLoadServerMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class Server {

    private final Javalin javalin;

    public Server() {
        var factory = new TypeFactory();
        if(true) {
            factory.useDatabase();
        } else {
            factory.useMemoryStorage();
        }
        factory.ensureDependencies();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        JsonEndpointHandler errorHandler = factory.getEndpointHandler(JsonEndpointHandler.class);
        javalin.exception(IllegalArgumentException.class,
                        (exception, context) ->
                                errorHandler.badRequest(context, exception.getMessage()))
               .exception(LoginException.class,
                       (exception, context) ->
                               errorHandler.unauthorized(context, exception.getMessage()))
               .exception(RuntimeException.class,
                       (exception, context) ->
                               errorHandler.errorMessageResult(context, 500, exception.getMessage()));

        javalin.delete("/db", factory.getEndpointHandler(ResetServerEndpointHandler.class));
        javalin.post("/user", factory.getEndpointHandler(RegisterUserEndpointHandler.class));
        javalin.post("/session", factory.getEndpointHandler(LoginEndpointHandler.class));
        javalin.delete("/session", factory.getEndpointHandler(LogoutEndpointHandler.class));
        javalin.post("/game", factory.getEndpointHandler(CreateGameEndpointHandler.class));
        javalin.put("/game", factory.getEndpointHandler(JoinGameEndpointHandler.class));
        javalin.get("/game", factory.getEndpointHandler(ListGamesEndpointHandler.class));

        HashMap<String, WsContext> clients = new HashMap<>();

        javalin.ws("/ws", config -> {
            config.onConnect(ctx -> {
                ctx.enableAutomaticPings();
                System.out.println("Websocket connected");
            });
            config.onMessage(ctx -> {
                var gson = factory.getGson();
                var sessionService = factory.getSessionService();
                var gameDataAccess = factory.getGameDataAccess();

                try
                {
                    UserGameCommand clientMessage = gson.fromJson(ctx.message(), UserGameCommand.class);

                    AuthData session = sessionService.validateSession(clientMessage.getAuthToken());

                    if(clientMessage.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                        clients.put(session.authToken(), ctx);

                        GameData gameData = gameDataAccess.getGameById(clientMessage.getGameID());
                        if(gameData == null) {
                            ServerErrorMessage message = new ServerErrorMessage("Invalid Game ID");
                            ctx.send(gson.toJson(message));
                            return;
                        }

                        GameLoadServerMessage message1 = new GameLoadServerMessage(gameData);
                        ctx.send(gson.toJson(message1));

                        for (var client : clients.entrySet())
                        {
                            String authToken = client.getKey();
                            WsContext clientContext = client.getValue();

                            if(clientContext.session.isOpen()) {
                                if(!authToken.equals(clientMessage.getAuthToken())) {
                                    try {
                                        NotificationMessage message = new NotificationMessage(session.username() + " has joined " + gameData.gameName());
                                        clientContext.send(gson.toJson(message));
                                    } catch(Exception e2) {
                                        //TODO: log
                                        System.out.println(e2.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }
                catch(LoginException ex)
                {
                    ServerErrorMessage message = new ServerErrorMessage("Invalid Auth Token");
                    ctx.send(gson.toJson(message));
                }
                catch(Exception ex)
                {
                    //TODO: log
                    System.out.println(ex.getMessage());

                    ServerErrorMessage message = new ServerErrorMessage("A fatal error has occurred.");
                    ctx.send(gson.toJson(message));
                }
            });
            config.onClose(_ -> System.out.println("Websocket closed"));
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
