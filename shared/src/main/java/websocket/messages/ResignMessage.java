package websocket.messages;

public class ResignMessage extends NotificationMessage {
    private final String username;

    public ResignMessage(String username) {
        super(username + " has resigned");

        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.username = username;
        this.isResignationNotice = true;
    }

    public String getUsername() {
        return username;
    }
}
