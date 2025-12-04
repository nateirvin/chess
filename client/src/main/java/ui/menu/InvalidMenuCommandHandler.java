package ui.menu;

import ui.BufferedRenderer;

public class InvalidMenuCommandHandler implements MenuCommandHandler {
    private final BufferedRenderer render;

    public InvalidMenuCommandHandler(BufferedRenderer render) {
        this.render = render;
    }

    @Override
    public String execute(String... arguments) {
        render.error("Unknown or invalid command");
        return null;
    }
}
