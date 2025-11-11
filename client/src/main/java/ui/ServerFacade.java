package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class ServerFacade
{
    private final Gson gson;
    private String host;
    private int port;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public ServerFacade(Gson gson)
    {
        this.gson = gson;
    }

    public void bindTo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SessionData registerUser(String userName, String plainTextPassword, String email)
                            throws IOException, InterruptedException
    {
        RegisterRequest request = new RegisterRequest(userName, plainTextPassword, email);

        String requestJson = gson.toJson(request);
        var requestBody = HttpRequest.BodyPublishers.ofString(requestJson);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(getUri("user"))
                .header("Content-Type", "application/json")
                .POST(requestBody)
                .build();

        LoginResult loginResult;
        try {
            loginResult = sendAndReceive(httpRequest, LoginResult.class);
        } catch (HttpFailureException e) {
            return null;
        }

        return new SessionData(loginResult.authToken(),
                               new UserData(userName, plainTextPassword, email));
    }

    private URI getUri(String path) {
        try {
            String urlString = String.format(Locale.getDefault(), "http://%s:%d/%s", host, port, path);
            return new URI(urlString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T sendAndReceive(HttpRequest httpRequest, Class<T> clazz)
                    throws IOException, InterruptedException, HttpFailureException
    {
        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        if(response.statusCode() == 403)
        {
            throw new HttpFailureException(response.statusCode());
        }

        try (InputStream responseBody = response.body())
        {
            try (InputStreamReader reader = new InputStreamReader(responseBody))
            {
                return gson.fromJson(reader, clazz);
            }
        }
    }

    //TODO: actually implement
    public SessionData loginUser(String username, String plainTextPassword) {
        return new SessionData(UUID.randomUUID().toString(), new UserData("bob", "", ""));
    }

    public void logoutUser(String authToken) {
        //TODO: actually implement
    }

    public void createGame(String gameName) {
        //TODO: actually implement
    }

    //TODO: actually implement
    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> games = new ArrayList<>();
        games.add(new GameData(222, "strong", "flek", "weep"));
        games.add(new GameData(323, "jacob"));
        for(var g : games) {
            ChessGame game = new ChessGame();
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            g.setGame(game);
        }
        return games;
    }

    public String joinGame(int gameId, int userId, ChessGame.TeamColor color) {
        return null;  //TODO: actually implement
    }
}
