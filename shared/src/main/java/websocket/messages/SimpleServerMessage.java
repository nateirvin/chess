package websocket.messages;

public class SimpleServerMessage extends ServerMessage {
    private final String message;

    public SimpleServerMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
