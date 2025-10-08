package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import models.GameData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

public class ListGamesHandler extends JsonHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        ArrayList<GameData> games = new ArrayList<>();
        games.add(new GameData(1, "first", "john","bob"));
        var gamesMap = Map.of("games", games);

        successResult(context, gamesMap);
    }
}
