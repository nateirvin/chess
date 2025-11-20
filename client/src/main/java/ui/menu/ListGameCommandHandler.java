package ui.menu;

import ui.BufferedRenderer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ListGameCommandHandler implements MenuCommandHandler {
    private final Logger logger;
    private final BufferedRenderer render;

    public ListGameCommandHandler(Logger logger, BufferedRenderer render) {
        this.logger = logger;
        this.render = render;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length != 0) {
            return "Unknown arguments";
        }

        try {
            render.gamesListWithAltText();
        } catch(Exception e) {
            logger.log(Level.SEVERE, "Game listing failure", e);
            return "Could not get games list";
        }

        return null;
    }
}
