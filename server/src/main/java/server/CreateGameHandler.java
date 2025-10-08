package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.CreateGameRequest;
import model.GameData;
import org.jetbrains.annotations.NotNull;

public class CreateGameHandler extends JsonHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        CreateGameRequest request = getBodyObject(context, CreateGameRequest.class);

        GameData gameData = new GameData(1, null, null, null);

        successResult(context, gameData);
    }
}
