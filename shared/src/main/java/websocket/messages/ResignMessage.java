package websocket.messages;

import chess.ChessGame;

public class ResignMessage extends NotificationMessage {
    private final String username;
    private final ChessGame.TeamColor color;

    public ResignMessage(ChessGame.TeamColor color, String username) {
        super("%s (%s) has resigned".formatted(username, color));

        if(username == null || username.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.color = color;
        this.username = username;
        this.isResignationNotice = true;
    }

    public String getUsername() {
        return username;
    }
}
