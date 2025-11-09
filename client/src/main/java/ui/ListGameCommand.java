package ui;

public class ListGameCommand implements MenuCommand {
    private final GameListDisplay displayer;

    public ListGameCommand(GameListDisplay displayer) {
        this.displayer = displayer;
    }
    @Override
    public String execute(String... arguments) {
        if(arguments.length != 0) {
            return "Unknown arguments";
        }

        displayer.showGamesListWithAlternateText("No games yet; use the 'create' command to start one!");
        System.out.println();

        return null;
    }
}
