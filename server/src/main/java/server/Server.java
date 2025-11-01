package server;

import io.javalin.*;
import util.SerializerFactory;

import javax.security.auth.login.LoginException;

public class Server {

    private final Javalin javalin;

    public Server() {
        var factory = new HandlerFactory(new SerializerFactory());
        if(true) {
            factory.useDatabase();
        } else {
            factory.useMemoryStorage();
        }
        factory.ensureDependencies();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        JsonHandler errorHandler = factory.getHandler(JsonHandler.class);
        javalin.exception(IllegalArgumentException.class,
                        (exception, context) ->
                                errorHandler.badRequest(context, exception.getMessage()))
               .exception(LoginException.class,
                       (exception, context) ->
                               errorHandler.unauthorized(context, exception.getMessage()))
               .exception(RuntimeException.class,
                       (exception, context) ->
                               errorHandler.errorMessageResult(context, 500, exception.getMessage()));

        javalin.delete("/db", factory.getHandler(ResetServerHandler.class));
        javalin.post("/user", factory.getHandler(RegisterUserHandler.class));
        javalin.post("/session", factory.getHandler(LoginHandler.class));
        javalin.delete("/session", factory.getHandler(LogoutHandler.class));
        javalin.post("/game", factory.getHandler(CreateGameHandler.class));
        javalin.put("/game", factory.getHandler(JoinGameHandler.class));
        javalin.get("/game", factory.getHandler(ListGamesHandler.class));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
