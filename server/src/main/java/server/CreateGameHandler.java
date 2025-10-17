package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.CreateGameRequest;
import model.GameData;
import org.jetbrains.annotations.NotNull;

public class CreateGameHandler extends JsonHandler implements Handler
{
    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        validateLogin(context);

        CreateGameRequest request = getBodyObject(context, CreateGameRequest.class);
        validate("gameName", request.gameName());

        GameData gameData = this.gameService.createGame(request);

        successResult(context, gameData);
    }
}
