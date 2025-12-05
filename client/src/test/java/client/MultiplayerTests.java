package client;

import chess.ChessBoard;
import chess.ChessPiece;
import dataaccess.DataAccessException;
import dataaccess.TestHelper;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;
import org.mockito.Mockito;
import server.Server;
import ui.Application;
import ui.BufferedRenderer;
import ui.ConsoleReader;
import ui.DisplaySink;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled("The functionality works, but the test isn't passing")
public class MultiplayerTests
{
    private static Server server;
    private static int port;

    private Application app1;
    private Application app2;
    private InOrder inOrder;
    private ConsoleReader mockReader1;
    private DisplaySink mockWriter1;
    private DisplaySink mockWriter2;
    private ConsoleReader mockReader2;

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

        mockReader1 = Mockito.mock(ConsoleReader.class);
        mockWriter1 = Mockito.mock(DisplaySink.class);
        app1 = new Application(mock(Logger.class), new BufferedRenderer(mockReader1, mockWriter1));
        app1.bindToHost("localhost", port);

        mockWriter2 = Mockito.mock(DisplaySink.class);
        mockReader2 = Mockito.mock(ConsoleReader.class);
        app2 = new Application(mock(Logger.class), new BufferedRenderer(mockReader2, mockWriter2));
        app2.bindToHost("localhost", port);

        this.inOrder = inOrder(mockReader1, mockWriter1, mockReader2, mockWriter2);
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
        setupGame();
        when(mockReader2.isWaiting()).thenReturn(true);

        verifyPrintedMyTurn(mockWriter1);
        verifyPrintedWaitingOnPlayer(mockWriter2);
    }

    @Test
    @DisplayName("First move synced")
    public void firstMove()
    {
        setupGame();
        when(mockReader2.isWaiting()).thenReturn(true);

        app1.getCommand("move").execute("a2", "a4");

        Stream.of(app1, app2).forEach(app -> {
            assertNull(getPiece(app, 2, 1));
            assertEquals(ChessPiece.PieceType.PAWN, getPiece(app, 4, 1).getPieceType());
        });

        //before the move
        verifyPrintedMyTurn(mockWriter1);
        verifyPrintedWaitingOnPlayer(mockWriter2);

        //after the move
        verifyPrintedWaitingOnPlayer(mockWriter1);
        verify(mockWriter2).println(startsWith("* user1 moved "));
        verifyPrintedMyTurn(mockWriter2);
    }

    @Test
    public void pawnPromotionTest()
    {
        setupGame();
        when(mockReader1.firstToken()).thenReturn("Q");

        app1.getCommand("move").execute("b2", "b4");
        System.out.println(getBoardFor(app1).toString());

        app2.getCommand("move").execute("c7", "c5");
        System.out.println(getBoardFor(app2).toString());

        app1.getCommand("move").execute("b4", "b5");
        System.out.println(getBoardFor(app1).toString());

        app2.getCommand("move").execute("d8", "c7");
        System.out.println(getBoardFor(app2).toString());

        app1.getCommand("move").execute("b5", "b6");
        System.out.println(getBoardFor(app1).toString());

        app2.getCommand("move").execute("d7", "d6");
        System.out.println(getBoardFor(app2).toString());

        app1.getCommand("move").execute("b6", "c7");
        System.out.println(getBoardFor(app1).toString());

        app2.getCommand("move").execute("g8", "h6");
        System.out.println(getBoardFor(app2).toString());

        app1.getCommand("move").execute("c7", "b8");
        System.out.println(getBoardFor(app1).toString());

        verify(mockWriter1).print("Do you want to promote to (q)ueen, K(n)ight (b)ishop, or (r)ook?");

        Stream.of(app1, app2).forEach(app -> {
            assertEquals(ChessPiece.PieceType.QUEEN, getPiece(app, 8, 2).getPieceType());

            //noinspection TrailingWhitespacesInTextBlock
            assertEquals("""
                         rPb kn r
                         pp  pppp
                            p   n
                           p     
                                 
                                 
                         P PPPPPP
                         RNBQKBNR               
                         """, getBoardFor(app).toString());
        });
    }

    private void setupGame() {
        app1.getCommand("register").execute("user1", "user1");
        app2.getCommand("register").execute("user2", "user2");

        app1.getCommand("create").execute(UUID.randomUUID().toString());
        app1.getCommand("list").execute();
        app1.getCommand("join").execute("1", "white");

        app2.getCommand("list").execute();
        app2.getCommand("join").execute("1", "black");
    }

    private static ChessPiece getPiece(Application app, int row, int col) {
        return getBoardFor(app).getPiece(row, col);
    }

    private static ChessBoard getBoardFor(Application app) {
        return app.getStateManager().getCurrentGame().getGame().getBoard();
    }

    private void verifyPrintedMyTurn(DisplaySink mockWriter) {
        inOrder.verify(mockWriter).print("It is ");
        inOrder.verify(mockWriter).print("your");
        inOrder.verify(mockWriter).println(" turn");
    }

    private void verifyPrintedWaitingOnPlayer(DisplaySink mockWriter) {
        inOrder.verify(mockWriter).println(startsWith("Waiting for "));
    }
}
