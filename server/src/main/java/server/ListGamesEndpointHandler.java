package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.GameData;
import model.GamesList;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.SessionService;
import service.UserService;
import java.util.ArrayList;

public class ListGamesEndpointHandler extends JsonEndpointHandler implements Handler
{
    public ListGamesEndpointHandler(Gson gson, UserService userService, SessionService sessionService, GameService gameService) {
        super(gson, userService, sessionService, gameService);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        validateLogin(context);

        ArrayList<GameData> games = this.gameService.getGames();

        var gamesMap = new GamesList(games);
        successResult(context, gamesMap);
    }
}
