package ui;

import chess.ChessGame;

public class JoinGameCommandHandler implements MenuCommandHandler {
    private final AppState appState;
    private final ServerFacade serverFacade;
    private final GameListDisplay displayer;

    public JoinGameCommandHandler(AppState appState, ServerFacade serverFacade, GameListDisplay displayer) {
        this.appState = appState;
        this.serverFacade = serverFacade;
        this.displayer = displayer;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length == 2) {
            String rawNumber = arguments[0];
            String rawColor = arguments[1] != null ? arguments[1] : "";

            int gameId;
            ChessGame.TeamColor color;
            try
            {
                int gameNumber = Integer.parseInt(rawNumber);
                gameId = displayer.getGameIdFromNumber(gameNumber);

                color = ChessGame.TeamColor.valueOf(rawColor.toUpperCase());
            }
            catch(NumberFormatException | IndexOutOfBoundsException ex)
            {
                return "Not a valid game number";
            }
            catch(IllegalArgumentException ex)
            {
                return "Not a valid team color";
            }

            String errorMessage = serverFacade.joinGame(gameId, appState.getUserID(), color);

            if(errorMessage == null) {
                System.out.println("Joined!");
                System.out.println();
                displayer.showGamesList();
            }

            return errorMessage;
        } else {
            return "Invalid arguments";
        }
    }
}
