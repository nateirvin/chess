package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.LoginResult;
import models.RegisterRequest;
import org.jetbrains.annotations.NotNull;

public class RegisterUserHandler extends JsonHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        RegisterRequest request = getBodyObject(context, RegisterRequest.class);

        LoginResult result = new LoginResult(request.username(), "abc");

        successResult(context, result);
    }
}
