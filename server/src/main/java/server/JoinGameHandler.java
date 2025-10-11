package server;

import chess.ChessGame;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.JoinGameRequest;
import org.jetbrains.annotations.NotNull;
import service.AlreadyTakenException;

public class JoinGameHandler extends JsonHandler implements Handler
{
    @Override
    public void handle(@NotNull Context context) throws Exception
    {
        AuthData session = validateLogin(context);

        JoinGameRequest request = getBodyObject(context, JoinGameRequest.class);
        validate("gameID", String.valueOf(request.gameID()));
        validate("color", request.playerColor());
        ChessGame.TeamColor color;
        if(request.playerColor().toUpperCase().equals("BLACK")) {
            color = ChessGame.TeamColor.BLACK;
        }
        else if(request.playerColor().toUpperCase().equals("WHITE")) {
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
