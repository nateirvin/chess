package ui;

public class MenuCommandFactory
{
    public static MenuCommand getCommand(String textCommand)
    {
        switch (textCommand) {
            case "register" -> {
                return new RegisterUserCommand();
            }
            case "login" -> {
                return new LoginUserCommand();
            }
            case "help" -> {
                return new HelpCommand();
            }
            case "quit", "exit" -> {
                return null;
            }
            default -> {
                return new InvalidMenuCommand();
            }
        }
    }
}
