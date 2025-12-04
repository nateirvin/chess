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

            String currentUserName = game.usernameFor(color);
            if(currentUserName != null) {
                if(currentUserName.equals(session.username())) {
                    return "You have already joined this game as this player.";
                } else {
                    return "You cannot join this game as that player.";
                }
            }

            try {
                serverFacade.joinGame(game.gameID(), appState.getSession(), color);
                render.userActionComplete("Joined!");

                UserGameCommand userCommand =
                        new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                                            session.authToken(),
                                            session.userId());
                webSocket.send(userCommand);
            } catch(ConnectException | DeploymentException e) {
                logger.log(Level.INFO, "Cannot connect", e);
                return "Game server cannot be reached.";
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failure in join game", e);
                return "Failed to join game.";
            }

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
