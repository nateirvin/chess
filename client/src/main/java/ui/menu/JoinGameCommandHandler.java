package ui.menu;

import chess.ChessGame;
import jakarta.websocket.DeploymentException;
import model.AuthData;
import model.GameData;
import model.UserEntryResult;
import ui.*;
import ui.data.AppState;
import ui.data.GameListAccessor;
import ui.data.ServerFacade;
import ui.data.WebSocketClient;
import websocket.commands.UserGameCommand;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JoinGameCommandHandler extends GameScopedCommandHandler implements MenuCommandHandler
{
    private final Logger logger;
    private final ServerFacade serverFacade;
    private final WebSocketClient webSocket;

    public JoinGameCommandHandler(AppState appState,
                                  BufferedRenderer render,
                                  Logger logger,
                                  GameListAccessor gameListAccessor,
                                  ServerFacade serverFacade,
                                  WebSocketClient webSocket)
    {
        super(appState, render, gameListAccessor);
        this.logger = logger;
        this.serverFacade = serverFacade;
        this.webSocket = webSocket;
    }

    @Override
    public String execute(String... arguments) {
        if(arguments.length == 2) {
            UserEntryResult<GameData> gameFetchResult = fetchGame(arguments[0]);
            if(!gameFetchResult.success()) {
                return gameFetchResult.getErrorMessage();
            }
            GameData game = gameFetchResult.getValue();

            UserEntryResult<ChessGame.TeamColor> colorResult = getTeamColor(arguments[1]);
            if(!colorResult.success()) {
                return colorResult.getErrorMessage();
            }
            ChessGame.TeamColor color = colorResult.getValue();

            AuthData session = appState.getSession();

            try {
                serverFacade.joinGame(game.gameID(), appState.getSession(), color);
            } catch(ConnectException ex) {
                logger.log(Level.INFO, "Cannot connect", ex);
                return "Game server cannot be reached.";
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failure in join game", ex);
                return "Failed to join game.";
            }
            appState.setPlayer(color);
            render.userActionComplete("Joined!");

            try {
                //calls back with Game state
                //notifies other users game has been joined
                UserGameCommand userCommand =
                        new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                                            session.authToken(),
                                            game.gameID());
                webSocket.send(userCommand);
            } catch(DeploymentException ex) {
                logger.log(Level.INFO, "Cannot connect", ex);
                return "Game server did not receive all updates.";
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failure in join game", ex);
                return "Failed to start game.";
            }

            render.waitForBoard();
            displayTurnPlayer();

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
