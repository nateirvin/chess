package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.LoginRequest;
import models.LoginResult;
import org.jetbrains.annotations.NotNull;

public class LoginHandler extends JsonHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        LoginRequest request = getBodyObject(context, LoginRequest.class);

        LoginResult result = new LoginResult(request.username(), "abc");

        successResult(context, result);
    }
}
