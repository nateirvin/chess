package ui.menu;

import chess.ChessGame;
import model.GameData;
import model.UserEntryResult;
import ui.*;

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
            int gameNumber = gameNumberResult.getValue();
            GameData game = displayer.getGameByNumber(gameNumber);
            if (game == null) {
                return "No such game.";
            }

            if(!colorResult.success()) {
                return colorResult.getErrorMessage();
            }
            ChessGame.TeamColor color = colorResult.getValue();

            String currentUserName = game.usernameFor(color);
            if(currentUserName != null) {
                if(currentUserName.equals(appState.currentUsername())) {
                    return "You have already joined this game as this player.";
                } else {
                    return "You cannot join this game as that player.";
                }
            }

            try {
                serverFacade.joinGame(game.gameID(), appState.getSession(), color);
            } catch(ConnectException e) {
                logger.log(Level.INFO, "Cannot connect", e);
                return "Game server cannot be reached.";
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failure in join game", e);
                return "Failed to join game.";
            }

            System.out.println("Joined!");
            assert appState.userIsLoggedIn();
            game.setPlayer(color, appState.currentUsername());

            ChessBoardRenderer renderer = new ChessBoardRenderer(ColorScheme.example());
            renderer.render(game.getGame().getBoard(), color);

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
