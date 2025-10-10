package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.GameData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class ListGamesHandler extends JsonHandler implements Handler
{
    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        validateLogin(context);

        ArrayList<GameData> games = this.gameService.getGames();

        var gamesMap = Map.of("games", games);
        successResult(context, gamesMap);
    }
}
