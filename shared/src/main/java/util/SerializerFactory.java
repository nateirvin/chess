package util;

import chess.ChessBoard;
import chess.ChessBoardSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializerFactory
{
    public Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ChessBoard.class, new ChessBoardSerializer());
        return gsonBuilder.create();
    }
}
