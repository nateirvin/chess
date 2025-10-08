package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.JoinGameRequest;
import org.jetbrains.annotations.NotNull;

public class JoinGameHandler extends JsonHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        JoinGameRequest request = getBodyObject(context, JoinGameRequest.class);
    }
}
