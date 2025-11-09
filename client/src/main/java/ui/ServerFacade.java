package ui;

import chess.ChessGame;
import model.GameData;
import model.SessionData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;

public class ServerFacade {
    //TODO: actually implement
    public SessionData registerUser(String userName, String plainTextPassword, String email) {
        return new SessionData(UUID.randomUUID().toString(), new UserData("bob", "", ""));
    }

    public void logoutUser(String authToken) {
        //TODO: actually implement
    }

    //TODO: actually implement
    public SessionData loginUser(String username, String plainTextPassword) {
        return new SessionData(UUID.randomUUID().toString(), new UserData("bob", "", ""));
    }

    public void createGame(String gameName) {
        //TODO: actually implement
    }

    //TODO: actually implement
    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> games = new ArrayList<>();
        games.add(new GameData(222, "strong", "flek", "weep"));
        games.add(new GameData(323, "jacob"));
        return games;
    }

    public String joinGame(int gameId, int userId, ChessGame.TeamColor color) {
        return null;  //TODO: actually implement
    }
}
