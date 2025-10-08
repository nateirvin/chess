package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

abstract class JsonHandler
{
    public <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }

    public <T> void successResult(@NotNull Context context, T obj)
    {
        context.contentType("application/json");
        context.result(new Gson().toJson(obj));
    }
}
