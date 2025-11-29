package server;

import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public interface MessageHandler
{
    ServerMessage handle(UserGameCommand clientMessage);
}
