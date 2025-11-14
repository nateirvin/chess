package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.SessionService;
import service.UserService;

public class LoginEndpointHandler extends JsonEndpointHandler implements Handler
{
    public LoginEndpointHandler(Gson gson, UserService userService, SessionService sessionService, GameService gameService) {
        super(gson, userService, sessionService, gameService);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        LoginRequest request = getBodyObject(context, LoginRequest.class);
        validate("username", request.username());
        validate("password", request.password());

        AuthData session = userService.login(request);
        LoginResult result = new LoginResult(session);

        successResult(context, result);
    }
}
