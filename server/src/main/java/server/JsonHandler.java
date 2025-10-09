package server;

import com.google.gson.Gson;
import dataaccess.GameMemoryProvider;
import dataaccess.SessionMemoryProvider;
import dataaccess.UsersMemoryProvider;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import service.GameService;
import service.SessionService;
import service.UserService;

import javax.security.auth.login.LoginException;
import java.util.Map;

abstract class JsonHandler
{
    protected final Gson serialize;
    protected final SessionService sessionService;
    protected final UserService userService;
    protected final GameService gameService;

    public JsonHandler()
    {
        this.serialize = new Gson();
        this.sessionService = new SessionService(new SessionMemoryProvider());
        this.userService = new UserService(sessionService, new UsersMemoryProvider());
        this.gameService = new GameService(new GameMemoryProvider());
    }

    @Nullable
    protected String validateLogin(@NotNull Context context) throws LoginException
    {
        String authToken = context.header("authorization");
        this.sessionService.validateSession(authToken);
        return authToken;
    }

    protected <T> T getBodyObject(Context context, Class<T> clazz)
    {
        T bodyObject = serialize.fromJson(context.body(), clazz);

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
     * @param context
     * @param message
     */
    protected void unauthorized(@NotNull Context context, @NotNull String message)
    {
        errorMessageResult(context, 401, message);
    }

    /**
     * Returns HTTP 403
     * @param context
     * @param message
     */
    protected void forbidden(@NotNull Context context, @NotNull String message)
    {
        errorMessageResult(context, 403, message);
    }

    protected void internalError(@NotNull Context context, @NotNull String message)
    {
        errorMessageResult(context, 500, message);
    }

    protected <T> void successResult(@NotNull Context context, T obj)
    {
        statusObjectResult(context, 200, obj);
    }

    private void errorMessageResult(@NotNull Context context, int status, @NotNull String message)
    {
        statusObjectResult(context, status, Map.of("message", "Error: " + message));
    }

    private <T> void statusObjectResult(@NotNull Context context, int status, T obj)
    {
        context.status(status);
        context.contentType("application/json");
        context.result(serialize.toJson(obj));
    }
}
