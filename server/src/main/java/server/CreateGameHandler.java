package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.CreateGameRequest;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.SessionService;
import service.UserService;

public class CreateGameHandler extends JsonHandler implements Handler
{
    public CreateGameHandler(Gson gson, UserService userService, SessionService sessionService, GameService gameService) {
        super(gson, userService, sessionService, gameService);
    }

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
