package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import service.GameService;
import service.SessionService;
import service.UserService;
import javax.security.auth.login.LoginException;
import java.util.Map;

class JsonEndpointHandler
{
    protected final Gson gson;
    protected final SessionService sessionService;
    protected final UserService userService;
    protected final GameService gameService;

    public JsonEndpointHandler(Gson gson, UserService userService, SessionService sessionService, GameService gameService)
    {
        this.gson = gson;
        this.sessionService = sessionService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @Nullable
    protected AuthData validateLogin(@NotNull Context context) throws LoginException
    {
        String authToken = context.header("authorization");
        return this.sessionService.validateSession(authToken);
    }

    protected <T> T getBodyObject(Context context, Class<T> clazz)
    {
        T bodyObject = gson.fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new IllegalArgumentException("missing required body");
        }

        return bodyObject;
    }

    protected static void validate(String entityKind, String entity)
    {
        if(entity == null || entity.trim().isEmpty())
        {
            throw new IllegalArgumentException("The " + entityKind + " is required.");
        }
    }

    protected void badRequest(@NotNull Context context, @NotNull String message)
    {
        errorMessageResult(context, 400, message);
    }

    /**
     * Returns HTTP 401
     * @param context HTTP context
     * @param message error message
     */
    protected void unauthorized(@NotNull Context context, @NotNull String message)
    {
        errorMessageResult(context, 401, message);
    }

    /**
     * Returns HTTP 403
     * @param context HTTP context
     * @param message error message
     */
    protected void forbidden(@NotNull Context context, @NotNull String message)
    {
        errorMessageResult(context, 403, message);
    }

    protected <T> void successResult(@NotNull Context context, T obj)
    {
        statusObjectResult(context, 200, obj);
    }

    protected void errorMessageResult(@NotNull Context context, int status, @NotNull String message)
    {
        statusObjectResult(context, status, Map.of("message", "Error: " + message));
    }

    private <T> void statusObjectResult(@NotNull Context context, int status, T obj)
    {
        context.status(status);
        context.contentType("application/json");
        context.result(gson.toJson(obj));
    }
}
