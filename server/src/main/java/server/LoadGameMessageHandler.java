package server;

import dataaccess.GameDataAccess;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.GameLoadServerMessage;
import websocket.messages.ServerMessage;

public class LoadGameMessageHandler implements MessageHandler
{
    private final GameDataAccess dataAccess;

    public LoadGameMessageHandler(GameDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ServerMessage handle(UserGameCommand clientMessage)
    {
        assert clientMessage != null;
        assert clientMessage.getCommandType() == UserGameCommand.CommandType.CONNECT;

        int gameID = clientMessage.getGameID();
        GameData gameData = dataAccess.getGameById(gameID);

        return new GameLoadServerMessage(gameData);
    }
}
