package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import org.jetbrains.annotations.NotNull;

public class LogoutHandler extends JsonHandler implements Handler
{
    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        AuthData session = validateLogin(context);

        assert session != null;
        assert session.authToken() != null;

        this.sessionService.closeSession(session.authToken());
    }
}
