package ui;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

class ConsoleReader implements Closeable
{
    private final Scanner inputReader;
    private String[] captures;

    public ConsoleReader()
    {
        this.inputReader = new Scanner(System.in);
    }

    public boolean read() {
        String input = inputReader.nextLine();
        String fullText = input != null ? input.trim() : "";
        this.captures = Arrays.stream(fullText.split(" ")).map(String::trim).toArray(String[]::new);
        return true;
    }

    public String firstToken() {
        if(captures.length > 0)
        {
            return captures[0].trim();
        }
        return null;
    }

    public String[] allButFirstToken() {
        return Arrays.stream(captures).skip(1).toArray(String[]::new);
    }

    @Override
    public void close() throws IOException {
        inputReader.close();
    }
}
