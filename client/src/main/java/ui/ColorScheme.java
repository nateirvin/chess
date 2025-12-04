package ui;

public class ColorScheme {
    private String borderBackground;
    private String borderText;
    private String lightSquareBackground;
    private String darkSquareBackground;
    private String lightHighlightSquareBackground;
    private String darkHighlightSquareBackground;
    private String player1Text;
    private String player1TextColorName;
    private String player2Text;
    private String player2TextColorName;
    private String aggressorText;
    private String aggressorBackground;

    public static ColorScheme example() {
        ColorScheme colorScheme = new ColorScheme();
        colorScheme.borderBackground = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        colorScheme.borderText = EscapeSequences.SET_TEXT_COLOR_BLACK;
        colorScheme.lightSquareBackground = EscapeSequences.SET_BG_COLOR_WHITE;
        colorScheme.darkSquareBackground = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        colorScheme.player1Text = EscapeSequences.SET_TEXT_COLOR_RED;
        colorScheme.player1TextColorName = "red";
        colorScheme.player2Text = EscapeSequences.SET_TEXT_COLOR_BLUE;
        colorScheme.player2TextColorName = "blue";
        colorScheme.aggressorText = EscapeSequences.SET_TEXT_COLOR_BLACK;
        colorScheme.aggressorBackground = EscapeSequences.SET_BG_COLOR_YELLOW;
        colorScheme.lightHighlightSquareBackground = EscapeSequences.SET_BG_COLOR_GREEN;
        colorScheme.darkHighlightSquareBackground = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        return colorScheme;
    }

    public static ColorScheme lighter() {
        ColorScheme colorScheme = new ColorScheme();
        colorScheme.borderBackground = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        colorScheme.borderText = EscapeSequences.SET_TEXT_COLOR_WHITE;
        colorScheme.lightSquareBackground = EscapeSequences.SET_BG_COLOR_WHITE;
        colorScheme.darkSquareBackground = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        colorScheme.player1Text = EscapeSequences.SET_TEXT_COLOR_RED;
        colorScheme.player1TextColorName = "red";
        colorScheme.player2Text = EscapeSequences.SET_TEXT_COLOR_BLUE;
        colorScheme.player2TextColorName = "blue";
        colorScheme.aggressorText = EscapeSequences.SET_TEXT_COLOR_BLACK;
        colorScheme.aggressorBackground = EscapeSequences.SET_BG_COLOR_YELLOW;
        colorScheme.lightHighlightSquareBackground = EscapeSequences.SET_BG_COLOR_GREEN;
        colorScheme.darkHighlightSquareBackground = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
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

    public String player1TextColorName() {
        return player1TextColorName;
    }

    public String forPlayer2Text() {
        return player2Text;
    }

    public String player2TextColorName() {
        return player2TextColorName;
    }

    public String forAggressorText() {
        return aggressorText;
    }

    public String forAggressorBackground() {
        return aggressorBackground;
    }

    public String forLightHighlightSquare() {
        return lightHighlightSquareBackground;
    }

    public String forDarkHighlightSquare() {
        return darkHighlightSquareBackground;
    }
}
