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

        LoginResult loginResult = sendUserRequest("user", request, 403);
        if (loginResult == null) {
            return null;
        }

        UserData userData = new UserData(loginResult.username(), plainTextPassword, email);
        userData.setId(loginResult.userId());
        return new SessionData(loginResult.authToken(), userData);
    }

    public SessionData loginUser(String username, String plainTextPassword) throws IOException, InterruptedException
    {
        LoginRequest request = new LoginRequest(username, plainTextPassword);

        LoginResult loginResult = sendUserRequest("session", request, 401);
        if (loginResult == null) {
            return null;
        }

        UserData userData = new UserData(loginResult.username(), plainTextPassword, "");
        userData.setId(loginResult.userId());
        return new SessionData(loginResult.authToken(), userData);
    }

    private <T> LoginResult sendUserRequest(String path, T request, int handleableCode)
                            throws IOException, InterruptedException
    {
        HttpRequest.BodyPublisher requestBody = toRequestBody(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(getUri(path))
                .header("Content-Type", "application/json")
                .POST(requestBody)
                .build();

        LoginResult loginResult;
        try
        {
            loginResult = sendAndReceive(httpRequest, LoginResult.class);
        }
        catch (HttpFailureException e)
        {
            if(e.getStatusCode() == handleableCode)
            {
                return null;
            }
            throw new RuntimeException(e);
        }

        return loginResult;
    }



    public void logoutUser(String authToken) throws IOException, InterruptedException {
        var request =
            HttpRequest.newBuilder()
                    .uri(getUri("session"))
                    .header("authorization", authToken)
                    .DELETE()
                    .build();

        try {
            send(request);
        } catch (HttpFailureException e) {
            throw new RuntimeException("Failed to logout", e);
        }
    }

    public void createGame(String authToken, String gameName)
                throws HttpFailureException, IOException, InterruptedException
    {
        CreateGameRequest request = new CreateGameRequest(gameName);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(getUri("game"))
                .header("authorization", authToken)
                .POST(toRequestBody(request)).build();

        send(httpRequest);
    }

    private URI getUri(String path) {
        try {
            String urlString = String.format(Locale.getDefault(), "http://%s:%d/%s", host, port, path);
            return new URI(urlString);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> HttpRequest.BodyPublisher toRequestBody(T request) {
        String requestJson = gson.toJson(request);
        return HttpRequest.BodyPublishers.ofString(requestJson);
    }

    private <T> T sendAndReceive(HttpRequest httpRequest, Class<T> clazz)
                    throws IOException, InterruptedException, HttpFailureException
    {
        HttpResponse<InputStream> response = send(httpRequest);

        try (InputStream responseBody = response.body())
        {
            try (InputStreamReader reader = new InputStreamReader(responseBody))
            {
                return gson.fromJson(reader, clazz);
            }
        }
    }

    private static HttpResponse<InputStream> send(HttpRequest httpRequest)
                    throws IOException, InterruptedException, HttpFailureException
    {
        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        if(response.statusCode() != 200)
        {
            throw new HttpFailureException(response.statusCode());
        }
        return response;
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
