package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameService;
import service.SessionService;
import service.UserService;
import util.SerializerFactory;

@SuppressWarnings("unchecked")
class HandlerFactory
{
    private final Gson gson;
    private final UserService userService;
    private final SessionService sessionService;
    private final GameService gameService;

    public HandlerFactory(SerializerFactory serializerFactory)
    {
        this.gson = serializerFactory.getGson();
        this.sessionService = new SessionService(new SessionMySqlProvider());
        this.userService = new UserService(sessionService, new UsersMySqlProvider());
        this.gameService = new GameService(new GameMySqlProvider());
    }

    public void ensureDependencies()
    {
        try{
            DatabaseManager.createDatabase();
            UsersMySqlProvider.createTables();
            SessionMySqlProvider.createTables();
            GameMySqlProvider.createTables();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends JsonHandler> T getHandler(Class<T> clazz)
    {
        if(clazz == ResetServerHandler.class)
        {
            return (T) new ResetServerHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == RegisterUserHandler.class)
        {
            return (T) new RegisterUserHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == LoginHandler.class)
        {
            return (T) new LoginHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == LogoutHandler.class)
        {
            return (T) new LogoutHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == CreateGameHandler.class)
        {
            return (T) new CreateGameHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == JoinGameHandler.class)
        {
            return (T) new JoinGameHandler(gson, userService, sessionService, gameService);
        }
        if(clazz == ListGamesHandler.class)
        {
            return (T) new ListGamesHandler(gson, userService, sessionService, gameService);
        }

        return (T) new JsonHandler(gson, userService, sessionService, gameService);
    }
}
