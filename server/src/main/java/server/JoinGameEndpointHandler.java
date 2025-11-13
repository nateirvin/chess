package server;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.JoinGameRequest;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;
import service.GameService;
import service.SessionService;
import service.UserService;

public class JoinGameEndpointHandler extends JsonEndpointHandler implements Handler
{
    public JoinGameEndpointHandler(Gson gson, UserService userService, SessionService sessionService, GameService gameService) {
        super(gson, userService, sessionService, gameService);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        AuthData session = validateLogin(context);
        assert session != null;
        assert session.username() != null;

        JoinGameRequest request = getBodyObject(context, JoinGameRequest.class);
        validate("gameID", String.valueOf(request.gameID()));
        validate("color", request.playerColor());
        ChessGame.TeamColor color;
        if(request.playerColor().equalsIgnoreCase("BLACK")) {
            color = ChessGame.TeamColor.BLACK;
        }
        else if(request.playerColor().equalsIgnoreCase("WHITE")) {
            color = ChessGame.TeamColor.WHITE;
        } else {
            throw new IllegalArgumentException("invalid color");
        }

        try
        {
            boolean joined = this.gameService.joinGame(request.gameID(), color, session.username());
            if(!joined) {
                throw new IllegalArgumentException("Invalid gameID");
            }
        }
        catch(AlreadyTakenException theftAttemptException)
        {
            forbidden(context, theftAttemptException.getMessage());
        }
    }
}
