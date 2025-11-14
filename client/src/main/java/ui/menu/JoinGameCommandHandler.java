package ui.menu;

import chess.ChessGame;
import model.UserEntryResult;
import ui.AppState;
import ui.GameListDisplay;
import ui.ServerFacade;

import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinGameCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    private final AppState appState;
    private final Logger logger;
    private final ServerFacade serverFacade;

    public JoinGameCommandHandler(AppState appState, Logger logger, ServerFacade serverFacade, GameListDisplay displayer)
    {
        super(displayer);
        this.appState = appState;
        this.logger = logger;
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

            String errorMessage;

            try {
                int gameId = gameNumberResult.getValue();
                ChessGame.TeamColor color = colorResult.getValue();
                serverFacade.joinGame(gameId, appState.getSession(), color);
            } catch(ConnectException e) {
                logger.log(Level.INFO, "Cannot connect", e);
                return "Game server cannot be reached.";
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failure in join game", e);
                return "Failed to join game.";
            }

            System.out.println("Joined!");
            System.out.println();
            displayer.showGamesList();

            return null;
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
