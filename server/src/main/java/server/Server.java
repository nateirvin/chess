package server;

import io.javalin.*;
import service.AlreadyTakenException;

import javax.security.auth.login.LoginException;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        var exceptionHandler = new ExceptionHandler();
        javalin.exception(AlreadyTakenException.class, exceptionHandler)
               .exception(IllegalArgumentException.class, exceptionHandler)
               .exception(LoginException.class, exceptionHandler);

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
