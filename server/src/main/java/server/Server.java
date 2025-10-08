package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
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
