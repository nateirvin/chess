package websocket.messages;

import chess.ChessGame;

public class ResignMessage extends NotificationMessage {
    private final String username;

    public ResignMessage(ChessGame.TeamColor color, String username) {
        super("%s (%s) has resigned".formatted(username, color));

        if(username == null || username.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEmpty() {
        //indicates incomplete serialization
        return username == null;
    }
}
