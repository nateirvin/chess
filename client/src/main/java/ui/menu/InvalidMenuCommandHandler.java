package ui.menu;

import ui.ErrorRenderer;

public class InvalidMenuCommandHandler implements MenuCommandHandler {
    @Override
    public String execute(String... arguments) {
        ErrorRenderer.print("Unknown command");
        return null;
    }
}
