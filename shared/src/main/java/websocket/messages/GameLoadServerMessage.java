package websocket.messages;

import model.GameData;

public class GameLoadServerMessage extends ServerMessage {
    private final GameData game;

    public GameLoadServerMessage(GameData gameData) {
        super(ServerMessageType.LOAD_GAME);
        this.game = gameData;
    }

    public GameData getGame() {
        return game;
    }
}
