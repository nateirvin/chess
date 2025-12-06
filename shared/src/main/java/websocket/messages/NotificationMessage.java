package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);

        if(message == null || message.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
