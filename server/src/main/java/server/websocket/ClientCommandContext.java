package server.websocket;

import chess.ChessGame;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;

class ClientCommandContext
{
    private final WsMessageContext caller;
    private final GameData game;
    private final AuthData session;
    private final UserGameCommand command;

    public ClientCommandContext(WsMessageContext caller, UserGameCommand command, AuthData session, GameData game) {
        this.caller = caller;
        this.command = command;
        this.session = session;
        this.game = game;
    }

    public WsMessageContext getCaller() {
        return caller;
    }

    public GameData getGame() {
        return game;
    }

    public AuthData getSession() {
        return session;
    }

    public UserGameCommand getCommand() {
        return command;
    }

    public UserGameCommand.CommandType getCommandType() {
        return command.getCommandType();
    }

    public String getPlayerRoleDescription() {
        ChessGame.TeamColor color = game.getColorForUser(session.username());
        return color == null ? "observer" : color.toString();
    }
}
