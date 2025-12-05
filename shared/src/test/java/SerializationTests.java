import chess.*;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.*;
import util.SerializerFactory;

public class SerializationTests
{
    private Gson gson;

    @BeforeEach
    public void setup()
    {
        gson = new SerializerFactory().getGson();
    }

    @Test
    public void toJsonForChessPosition()
    {
        ChessPosition position = new ChessPosition(5, 4);

        String actual = gson.toJson(position);

        Assertions.assertEquals("{\"row\":5,\"col\":4}", actual);
    }

    @Test
    public void fromJsonForChessPosition()
    {
        String json = "{\"row\":5,\"col\":4}";

        ChessPosition actual = gson.fromJson(json, ChessPosition.class);

        Assertions.assertEquals(5, actual.getRow());
        Assertions.assertEquals(4, actual.getColumn());
    }

    @Test
    public void toJsonForPawn()
    {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        String actual = gson.toJson(piece);

        Assertions.assertEquals("{\"pieceColor\":\"WHITE\",\"pieceType\":\"PAWN\"}", actual);
    }

    @Test
    public void fromJsonForPawn()
    {
        String json = "{\"pieceColor\":\"WHITE\",\"pieceType\":\"PAWN\"}";

        ChessPiece actual = gson.fromJson(json, ChessPiece.class);

        Assertions.assertEquals(ChessGame.TeamColor.WHITE, actual.getTeamColor());
        Assertions.assertEquals(ChessPiece.PieceType.PAWN, actual.getPieceType());
    }

    @Test
    public void toJsonForBishop()
    {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        String actual = gson.toJson(piece);

        Assertions.assertEquals("{\"pieceColor\":\"BLACK\",\"pieceType\":\"BISHOP\"}", actual);
    }

    @Test
    public void toJsonForRook()
    {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);

        String actual = gson.toJson(piece);

        Assertions.assertEquals("{\"pieceColor\":\"BLACK\",\"pieceType\":\"ROOK\"}", actual);
    }

    @Test
    public void toJsonForKnight()
    {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);

        String actual = gson.toJson(piece);

        Assertions.assertEquals("{\"pieceColor\":\"WHITE\",\"pieceType\":\"KNIGHT\"}", actual);
    }

    @Test
    public void toJsonForKing()
    {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        String actual = gson.toJson(piece);

        Assertions.assertEquals("{\"pieceColor\":\"WHITE\",\"pieceType\":\"KING\"}", actual);
    }

    @Test
    public void toJsonForQueen()
    {
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);

        String actual = gson.toJson(piece);

        Assertions.assertEquals("{\"pieceColor\":\"BLACK\",\"pieceType\":\"QUEEN\"}", actual);
    }

    @Test
    public void chessBoardFullCycleTest()
    {
        ChessBoard originalBoard = new ChessBoard();
        originalBoard.resetBoard();

        String json = gson.toJson(originalBoard);
        System.out.println(json);
        ChessBoard fetchedBoard = gson.fromJson(json, ChessBoard.class);

        Assertions.assertEquals(originalBoard, fetchedBoard);
    }

    @Test
    public void toJsonForChessGame()
    {
        ChessBoard board = new ChessBoard();
        ChessGame game = new ChessGame();
        game.setBoard(board);

        String actual = gson.toJson(game);

        Assertions.assertTrue(actual.startsWith("{\"currentTeam\":\"WHITE\",\"board\":"));
    }

    @Test
    public void toJsonForFullGameData()
    {
        ChessBoard board = new ChessBoard();
        ChessGame game = new ChessGame();
        game.setBoard(board);
        GameData gameData = new GameData(5, "zeppo", "hamilton","burr");
        gameData.setGame(game);

        String actual = gson.toJson(gameData);

        Assertions.assertTrue(
                actual.startsWith(
                        "{\"gameID\":5,\"gameName\":\"zeppo\"," +
                        "\"whiteUsername\":\"hamilton\",\"blackUsername\":\"burr\"," +
                        "\"game\":{\"currentTeam\":\"WHITE\",\"board\":"));
    }

    @Test
    public void jsonFullCycleTestPopulatedBoard()
    {
        ChessGame game = new ChessGame();

        String rep = gson.toJson(game);
        System.out.println(rep);
        ChessGame recycled = gson.fromJson(rep, ChessGame.class);

        Assertions.assertEquals(16,
                recycled.getBoard().teamPieces(ChessGame.TeamColor.WHITE).stream().count());
    }
}
