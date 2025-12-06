package ui;

import com.google.gson.Gson;
import ui.data.AppState;
import ui.data.GameListAccessor;
import ui.data.ServerFacade;
import ui.data.WebSocketClient;
import ui.menu.MenuCommandHandler;
import ui.menu.MenuCommandHandlerFactory;
import util.SerializerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

public class Application implements Closeable
{
    private final AppState appState;
    private final MenuCommandHandlerFactory menuCommandFactory;
    private final BufferedRenderer render;
    private final ServerFacade serverFacade;
    private final WebSocketClient webSocketClient;

    public Application(Logger logger, BufferedRenderer render)
    {
        Gson gson = new SerializerFactory().getGson();

        this.render = render;
        this.appState = new AppState();
        this.serverFacade = new ServerFacade(gson);
        this.webSocketClient = new WebSocketClient(appState, gson, render);

        menuCommandFactory =
            new MenuCommandHandlerFactory(
                    appState, logger, render,
                    new GameListAccessor(appState, serverFacade),
                    serverFacade, webSocketClient);
    }

    public AppState getStateManager() {
        return appState;
    }

    public BufferedRenderer getRenderer() {
        return render;
    }

    public void bindToHost(String host, int port) {
        serverFacade.bindTo(host, port);
        webSocketClient.bindTo(host, port);
    }

    public MenuCommandHandler getCommand(String commandName) {
        if (!appState.userIsLoggedIn()) {
            return menuCommandFactory.getPreLoginCommand(commandName);
        } else if(!appState.inGameplayMode()) {
            return menuCommandFactory.getPostLoginCommand(commandName);
        } else {
            return menuCommandFactory.getGameplayCommand(commandName);
        }
    }

    @Override
    public void close() throws IOException {
        this.serverFacade.close();
        this.webSocketClient.close();
    }
}
