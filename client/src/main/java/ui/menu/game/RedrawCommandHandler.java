package ui.menu.game;

import ui.BufferedRenderer;
import ui.menu.MenuCommandHandler;

public class RedrawCommandHandler implements MenuCommandHandler {
    private final BufferedRenderer render;

    public RedrawCommandHandler(BufferedRenderer render) {
        this.render = render;
    }

    @Override
    public String execute(String... arguments) {
        throw new RuntimeException("not implemented");
    }
}
