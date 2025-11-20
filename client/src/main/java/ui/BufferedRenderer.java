package ui;

public class BufferedRenderer {
    public void error(String message) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.println(">>> " + message);
        System.out.println();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }
}
