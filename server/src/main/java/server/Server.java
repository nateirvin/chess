package server;

import io.javalin.*;

import javax.security.auth.login.LoginException;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.exception(IllegalArgumentException.class,
                        (exception, context) ->
                                JsonHandler.badRequest(context, exception.getMessage()))
               .exception(LoginException.class,
                       (exception, context) ->
                               JsonHandler.unauthorized(context, exception.getMessage()))
               .exception(RuntimeException.class,
                       (e, context) -> JsonHandler.errorMessageResult(context, 500, e.getMessage()));

        javalin.delete("/db", new ResetServerHandler());
        javalin.post("/user", new RegisterUserHandler());
        javalin.post("/session", new LoginHandler());
        javalin.delete("/session", new LogoutHandler());
        javalin.post("/game", new CreateGameHandler());
        javalin.put("/game", new JoinGameHandler());
        javalin.get("/game", new ListGamesHandler());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
