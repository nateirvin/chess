package ui;

public class ListGameCommandHandler implements MenuCommandHandler {
    private final GameListDisplay displayer;

    public ListGameCommandHandler(GameListDisplay displayer) {
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
