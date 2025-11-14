package ui.menu;

import ui.GameListDisplay;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ListGameCommandHandler implements MenuCommandHandler {
    private final Logger logger;
    private final GameListDisplay displayer;

    public ListGameCommandHandler(Logger logger, GameListDisplay displayer) {
        this.logger = logger;
        this.displayer = displayer;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length != 0) {
            return "Unknown arguments";
        }

        try {
            displayer.showGamesListWithAlternateText("No games yet; use the 'create' command to start one!");
            System.out.println();
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Game listing failure", e);
            return "Could not get games list";
        }

        return null;
    }
}
