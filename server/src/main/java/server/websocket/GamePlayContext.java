package server.websocket;

import io.javalin.websocket.WsContext;
import model.AuthData;

public record GamePlayContext(AuthData loginInfo, WsContext client) {
}
