package ui;

public abstract class HelpCommand implements MenuCommand
{
    @Override
    public String execute(String... arguments) {
        printHelp();
        return null;
    }

    private void printHelp()
    {
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("  help : show this menu");
        specificCommands();
        System.out.println("  quit : exit the app");
        System.out.println();
    }
    
    protected abstract void specificCommands();
}
