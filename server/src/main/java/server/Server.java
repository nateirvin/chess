package server;

import io.javalin.*;
import util.SerializerFactory;

import javax.security.auth.login.LoginException;

public class Server {

    private final Javalin javalin;

    public Server() {
        var factory = new EndpointHandlerFactory(new SerializerFactory());
        if(true) {
            factory.useDatabase();
        } else {
            factory.useMemoryStorage();
        }
        factory.ensureDependencies();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        JsonEndpointHandler errorHandler = factory.getHandler(JsonEndpointHandler.class);
        javalin.exception(IllegalArgumentException.class,
                        (exception, context) ->
                                errorHandler.badRequest(context, exception.getMessage()))
               .exception(LoginException.class,
                       (exception, context) ->
                               errorHandler.unauthorized(context, exception.getMessage()))
               .exception(RuntimeException.class,
                       (exception, context) ->
                               errorHandler.errorMessageResult(context, 500, exception.getMessage()));

        javalin.delete("/db", factory.getHandler(ResetServerEndpointHandler.class));
        javalin.post("/user", factory.getHandler(RegisterUserEndpointHandler.class));
        javalin.post("/session", factory.getHandler(LoginEndpointHandler.class));
        javalin.delete("/session", factory.getHandler(LogoutEndpointHandler.class));
        javalin.post("/game", factory.getHandler(CreateGameEndpointHandler.class));
        javalin.put("/game", factory.getHandler(JoinGameEndpointHandler.class));
        javalin.get("/game", factory.getHandler(ListGamesEndpointHandler.class));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
