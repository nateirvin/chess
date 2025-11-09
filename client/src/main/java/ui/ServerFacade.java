package ui;

import model.GameData;

import java.util.ArrayList;
import java.util.UUID;

public class ServerFacade {
    public String registerUser(String userName, String plainTextPassword, String email) {
        return UUID.randomUUID().toString(); //TODO: actually implement
    }

    public void logoutUser(String authToken) {
        //TODO: actually implement
    }

    public String loginUser(String username, String plainTextPassword) {
        return UUID.randomUUID().toString(); //TODO: actually implement
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
}
