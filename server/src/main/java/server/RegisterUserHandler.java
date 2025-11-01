package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.LoginResult;
import model.RegisterRequest;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.GameService;
import service.SessionService;
import service.UserService;

public class RegisterUserHandler extends JsonHandler implements Handler
{
    public RegisterUserHandler(Gson gson, UserService userService, SessionService sessionService, GameService gameService) {
        super(gson, userService, sessionService, gameService);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        RegisterRequest request = getBodyObject(context, RegisterRequest.class);
        validate("username", request.username());
        validate("password", request.password());

        try
        {
            LoginResult result = userService.register(request);
            successResult(context, result);
        }
        catch(AlreadyTakenException alreadyTakenException)
        {
            forbidden(context, alreadyTakenException.getMessage());
        }
    }
}
