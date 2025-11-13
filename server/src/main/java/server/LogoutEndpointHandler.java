package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.SessionService;
import service.UserService;

public class LogoutEndpointHandler extends JsonEndpointHandler implements Handler
{
    public LogoutEndpointHandler(Gson gson, UserService userService, SessionService sessionService, GameService gameService) {
        super(gson, userService, sessionService, gameService);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        AuthData session = validateLogin(context);

        assert session != null;
        assert session.authToken() != null;

        this.sessionService.closeSession(session.authToken());
    }
}
