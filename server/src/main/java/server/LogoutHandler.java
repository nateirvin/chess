package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class LogoutHandler extends JsonHandler implements Handler
{
    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        String authToken = context.header("authorization");
        this.sessionService.validateSession(authToken);
        this.sessionService.closeSession(authToken);
    }
}
