package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String message;
    protected boolean isResignationNotice;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);

        if(message == null || message.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.message = message;
        this.isResignationNotice = false;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isResignation() {
        return isResignationNotice;
    }
}
