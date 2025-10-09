package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.LoginRequest;
import model.LoginResult;
import org.jetbrains.annotations.NotNull;

public class LoginHandler extends JsonHandler implements Handler
{
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
