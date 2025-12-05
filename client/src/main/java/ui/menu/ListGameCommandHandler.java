package ui.menu;

import model.GameData;
import ui.BufferedRenderer;
import ui.data.GameListAccessor;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListGameCommandHandler implements MenuCommandHandler {
    private final Logger logger;
    private final BufferedRenderer render;
    private final GameListAccessor gameListAccessor;

    public ListGameCommandHandler(Logger logger, BufferedRenderer render, GameListAccessor gameListAccessor) {
        this.logger = logger;
        this.render = render;
        this.gameListAccessor = gameListAccessor;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length != 0) {
            return "Unknown arguments";
        }

        try {
            ArrayList<GameData> games = gameListAccessor.loadGames();
            render.gamesListWithAltText(games);
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Game listing failure", e);
            return "Could not get games list";
        }

        return null;
    }
}
