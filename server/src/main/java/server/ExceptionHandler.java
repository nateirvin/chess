package server;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;

public class ExceptionHandler
       extends JsonHandler
       implements io.javalin.http.ExceptionHandler
{
    @Override
    public void handle(@NotNull Exception exception, @NotNull Context context) {
        if(exception.getClass() == IllegalArgumentException.class) {
            badRequest(context, exception.getMessage());
        }
        else if(exception.getClass() == AlreadyTakenException.class) {
            forbidden(context, exception.getMessage());
        }
        else {
            internalError(context, exception.getMessage());
        }
    }
}
