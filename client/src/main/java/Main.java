import ui.HelpCommand;
import ui.MenuCommand;
import ui.MenuCommandFactory;
import java.util.Arrays;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Welcome to the Chess app!");
        HelpCommand.printHelp();

        while(true)
        {
            System.out.print("CHESS (guest) $ ");

            String[] fullInput = getInputTokens();

            String commandName = null;
            if(fullInput.length > 0)
            {
                commandName = fullInput[0].trim().toLowerCase();
            }
            String[] menuArguments = Arrays.stream(fullInput).skip(1).toArray(String[]::new);

            MenuCommand command = MenuCommandFactory.getCommand(commandName);
            if(command == null)
            {
                System.out.println("Have a great day!");
                System.out.println();
                return;
            }
            command.execute(menuArguments);
        }
    }

    private static String[] getInputTokens() {
        Scanner inputReader = new Scanner(System.in);
        String input = inputReader.nextLine();
        String fullText = input != null ? input.trim().toLowerCase() : "";
        return fullText.split(" ");
    }
}