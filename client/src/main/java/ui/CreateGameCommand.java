package ui;

public class CreateGameCommand implements MenuCommand {
    private final ServerFacade serverFacade;
    private final GameListDisplay displayer;

    public CreateGameCommand(ServerFacade serverFacade, GameListDisplay displayer) {
        this.serverFacade = serverFacade;
        this.displayer = displayer;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length == 1) {
            String gameName = arguments[0];
            serverFacade.createGame(gameName);

            System.out.println("This game has been created!");
            System.out.println();
            displayer.showGamesList();

            return null;
        } else {
            return "Invalid arguments";
        }
    }
}
