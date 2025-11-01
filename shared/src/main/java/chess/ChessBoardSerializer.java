package chess;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ChessBoardSerializer implements JsonSerializer<ChessBoard>, JsonDeserializer<ChessBoard>
{
    @Override
    public JsonElement serialize(ChessBoard chessBoard, Type type, JsonSerializationContext jsonSerializationContext) {
        var squares = chessBoard.toSquares();
        return jsonSerializationContext.serialize(squares);
    }

    @Override
    public ChessBoard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        if(jsonElement.isJsonArray())
        {
            ChessSquare[] squares = jsonDeserializationContext.deserialize(jsonElement, ChessSquare[].class);
            ChessBoard board = new ChessBoard();
            for (ChessSquare square : squares)
            {
                board.addPiece(square.getPosition(), square.getPiece());
            }
            return board;
        }
        return null;
    }
}
