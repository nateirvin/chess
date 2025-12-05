package server;

import io.javalin.*;
import server.websocket.MessageRouter;
import javax.security.auth.login.LoginException;

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

        javalin.ws("/ws", config -> {
            config.onConnect(ctx -> {
                ctx.enableAutomaticPings();
                System.out.println("Websocket client connected");
            });
            config.onMessage(new MessageRouter(factory));
            config.onClose(x -> System.out.println("Websocket client disconnected"));
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
