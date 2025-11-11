package ui;

import chess.ChessGame;
import model.UserEntryResult;

public class JoinGameCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final ServerFacade serverFacade;

    public JoinGameCommandHandler(AppState appState, ServerFacade serverFacade, GameListDisplay displayer)
    {
        super(displayer);
        this.appState = appState;
        this.serverFacade = serverFacade;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length == 2) {
            UserEntryResult<Integer> gameNumberResult = getGameNumber(arguments[0]);
            UserEntryResult<ChessGame.TeamColor> colorResult = getTeamColor(arguments[1]);

            if(!gameNumberResult.success()) {
                return gameNumberResult.getErrorMessage();
            }
            if(!colorResult.success()) {
                return colorResult.getErrorMessage();
            }

            int gameId = gameNumberResult.getValue();
            ChessGame.TeamColor color = colorResult.getValue();
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

    private static UserEntryResult<ChessGame.TeamColor> getTeamColor(String rawValue) {
        try
        {
            String rawColor = rawValue != null ? rawValue : "";
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(rawColor.toUpperCase());
            return new UserEntryResult<>(color);
        }
        catch(IllegalArgumentException ex)
        {
            return new UserEntryResult<>("Not a valid team color");
        }
    }
}
