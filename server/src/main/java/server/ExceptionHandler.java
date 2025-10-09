package server;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.PermissionDeniedException;

import javax.security.auth.login.LoginException;

public class ExceptionHandler
       extends JsonHandler
       implements io.javalin.http.ExceptionHandler
{
    @Override
    public void handle(@NotNull Exception exception, @NotNull Context context)
    {
        if(exception.getClass() == IllegalArgumentException.class ||
           exception.getClass() == AlreadyTakenException.class)
        {
            badRequest(context, exception.getMessage());
        }
        else if(exception.getClass() == LoginException.class)
        {
            unauthorized(context, exception.getMessage());
        }
        else if(exception.getClass() == PermissionDeniedException.class)
        {
            forbidden(context, exception.getMessage());
        }
        else
        {
            internalError(context, exception.getMessage());
        }
    }
}
