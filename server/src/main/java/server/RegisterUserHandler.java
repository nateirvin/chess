package server;

import dataaccess.SessionMemoryProvider;
import dataaccess.UsersMemoryProvider;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.LoginResult;
import model.RegisterRequest;
import org.jetbrains.annotations.NotNull;
import service.SessionService;
import service.UserService;

public class RegisterUserHandler extends JsonHandler implements Handler
{
    private final UserService userService;

    public RegisterUserHandler()
    {
        SessionService sessionService = new SessionService(new SessionMemoryProvider());
        this.userService = new UserService(sessionService, new UsersMemoryProvider());
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        RegisterRequest request = getBodyObject(context, RegisterRequest.class);
        validate("username", request.username());
        validate("password", request.password());

        LoginResult result = userService.register(request);

        successResult(context, result);
    }

}
