package server;

import com.google.gson.Gson;
import dataaccess.*;
import server.websocket.LoadGameMessageHandler;
import server.websocket.MessageHandler;
import service.GameService;
import service.SessionService;
import service.UserService;
import util.SerializerFactory;
import websocket.commands.UserGameCommand;

public class TypeFactory
{
    private final Gson gson;
    private SessionDataAccess sessionDataAccess;
    private UsersDataAccess usersDataAccess;
    private GameDataAccess gameDataAccess;
    private UserService userService;
    private SessionService sessionService;
    private GameService gameService;

    public TypeFactory()
    {
        this.gson = new SerializerFactory().getGson();
    }

    public void useDatabase()
    {
        usersDataAccess = new UsersMySqlProvider();
        sessionDataAccess = new SessionMySqlProvider();
        gameDataAccess = new GameMySqlProvider(gson);
    }

    public void useMemoryStorage()
    {
        usersDataAccess = new UsersMemoryProvider();
        sessionDataAccess = new SessionMemoryProvider();
        gameDataAccess = new GameMemoryProvider();
    }

    public void ensureDependencies()
    {
        this.sessionService = new SessionService(sessionDataAccess);
        this.userService = new UserService(sessionService, usersDataAccess);
        this.gameService = new GameService(gameDataAccess);

        try{
            DatabaseManager.createDatabase();
            UsersMySqlProvider.createTables();
            SessionMySqlProvider.createTables();
            GameMySqlProvider.createTables();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Gson getGson() {
        return this.gson;
    }

    public SessionService getSessionService() {
        return this.sessionService;
    }

    @SuppressWarnings("unchecked")
    public <T extends JsonEndpointHandler> T getEndpointHandler(Class<T> clazz)
    {
        if(clazz == ResetServerEndpointHandler.class)
        {
            return (T) new ResetServerEndpointHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == RegisterUserEndpointHandler.class)
        {
            return (T) new RegisterUserEndpointHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == LoginEndpointHandler.class)
        {
            return (T) new LoginEndpointHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == LogoutEndpointHandler.class)
        {
            return (T) new LogoutEndpointHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == CreateGameEndpointHandler.class)
        {
            return (T) new CreateGameEndpointHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == JoinGameEndpointHandler.class)
        {
            return (T) new JoinGameEndpointHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == ListGamesEndpointHandler.class)
        {
            return (T) new ListGamesEndpointHandler(gson, userService, sessionService, gameService);
        }

        return (T) new JsonEndpointHandler(gson, userService, sessionService, gameService);
    }

    public MessageHandler getMessageHandler(UserGameCommand.CommandType commandType) {
        if(commandType == UserGameCommand.CommandType.CONNECT) {
            return new LoadGameMessageHandler(gameDataAccess);
        }
        return null;
    }
}
