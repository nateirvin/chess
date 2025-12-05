package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.DataAccessException;
import dataaccess.TestHelper;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;
import server.Server;
import ui.Application;
import ui.BufferedRenderer;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MultiplayerTests
{
    private static Server server;
    private static int port;

    private Application app1;
    private Application app2;
    private BufferedRenderer mockRender1;
    private BufferedRenderer mockRender2;
    private InOrder inOrder;

    @BeforeAll
    public static void init() throws DataAccessException
    {
        TestHelper.ensureDatabaseSetup();

        server = new Server();
        port = server.run(0); //allows Javalin to select its own port
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setupClients() throws DataAccessException {
        TestHelper.ensureDatabaseSetup();

        mockRender1 = createRenderMock();
        mockRender2 = createRenderMock();

        inOrder = inOrder(mockRender1, mockRender2);

        app1 = new Application(mock(Logger.class), mockRender1);
        app1.bindToHost("localhost", port);

        app2 = new Application(mock(Logger.class), mockRender2);
        app2.bindToHost("localhost", port);
    }

    private static BufferedRenderer createRenderMock() {
        var mockRender = mock(BufferedRenderer.class);
        doCallRealMethod().when(mockRender).waitForBoard();
        doCallRealMethod().when(mockRender).updateBoard(any(ChessBoard.class), any(ChessGame.TeamColor.class));
        return mockRender;
    }

    @AfterEach
    public void closeClients() throws IOException {
        app1.close();
        app2.close();
    }

    @Test
    @DisplayName("Two Users join game")
    public void basicSetup()
    {
        app1.getCommand("register").execute("user1", "user1");
        app2.getCommand("register").execute("user2", "user2");

        app1.getCommand("create").execute(UUID.randomUUID().toString());
        app1.getCommand("list").execute();
        app1.getCommand("join").execute("1", "white");

        app2.getCommand("list").execute();
        app2.getCommand("join").execute("1", "black");

        verify(mockRender1).myTurn();
        verify(mockRender1, never()).waitingOnPlayer(anyString());
        verify(mockRender2, never()).myTurn();
        verify(mockRender2).waitingOnPlayer("user1");
    }

    @Test
    @DisplayName("First move synced")
    public void firstMove()
    {
        app1.getCommand("register").execute("user1", "user1");
        app2.getCommand("register").execute("user2", "user2");

        app1.getCommand("create").execute(UUID.randomUUID().toString());
        app1.getCommand("list").execute();
        app1.getCommand("join").execute("1", "white");

        app2.getCommand("list").execute();
        app2.getCommand("join").execute("1", "black");

        app1.getCommand("move").execute("a2", "a4");

        assertNull(getPiece(app1, 2, 1));
        assertEquals(ChessPiece.PieceType.PAWN, getPiece(app1, 4, 1).getPieceType());
        assertNull(getPiece(app2, 2, 1));
        assertEquals(ChessPiece.PieceType.PAWN, getPiece(app2, 4, 1).getPieceType());

        inOrder.verify(mockRender1).myTurn();
        inOrder.verify(mockRender2).waitingOnPlayer("user1");
        inOrder.verify(mockRender1).waitingOnPlayer("user2");
    }

    private ChessPiece getPiece(Application app, int row, int col) {
        return app.getStateManager().getCurrentGame().getGame().getBoard().getPiece(row, col);
    }
}
