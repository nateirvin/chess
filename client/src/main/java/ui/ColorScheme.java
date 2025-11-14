package ui;

public class ColorScheme {
    private String borderBackground;
    private String borderText;
    private String lightSquareBackground;
    private String darkSquareBackground;
    private String player1Text;
    private String player2Text;

    public static ColorScheme example() {
        ColorScheme colorScheme = new ColorScheme();
        colorScheme.borderBackground = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        colorScheme.borderText = EscapeSequences.SET_TEXT_COLOR_BLACK;
        colorScheme.lightSquareBackground = EscapeSequences.SET_BG_COLOR_WHITE;
        colorScheme.darkSquareBackground = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        colorScheme.player1Text = EscapeSequences.SET_TEXT_COLOR_RED;
        colorScheme.player2Text = EscapeSequences.SET_TEXT_COLOR_BLUE;
        return colorScheme;
    }

    public static ColorScheme lighter() {
        ColorScheme colorScheme = new ColorScheme();
        colorScheme.borderBackground = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        colorScheme.borderText = EscapeSequences.SET_TEXT_COLOR_WHITE;
        colorScheme.lightSquareBackground = EscapeSequences.SET_BG_COLOR_WHITE;
        colorScheme.darkSquareBackground = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        colorScheme.player1Text = EscapeSequences.SET_TEXT_COLOR_RED;
        colorScheme.player2Text = EscapeSequences.SET_TEXT_COLOR_BLUE;
        return colorScheme;
    }

    public String forBorderBackground() {
        return borderBackground;
    }

    public String forBorderText() {
        return borderText;
    }

    public String forLightSquareBackground() {
        return lightSquareBackground;
    }

    public String forDarkSquareBackground() {
        return darkSquareBackground;
    }

    public String forPlayer1Text() {
        return player1Text;
    }

    public String forPlayer2Text() {
        return player2Text;
    }
}
