package ui.menu;

import ui.BufferedRenderer;

public abstract class HelpCommandHandler implements MenuCommandHandler
{
    protected final BufferedRenderer render;

    public HelpCommandHandler(BufferedRenderer render) {
        this.render = render;
    }

    @Override
    public String execute(String... arguments) {
        printHelp();
        return null;
    }

    private void printHelp()
    {
        render.helpMenuStart();
        render.helpMenuItem("help", "show this menu");
        printCommands();
        render.helpMenuItem("quit", "exit the app");
        render.helpMenuEnd();
    }
    
    protected abstract void printCommands();
}
