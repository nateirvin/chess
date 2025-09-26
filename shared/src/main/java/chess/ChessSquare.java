package chess;

public class ChessSquare {
    private final ChessPosition position;
    private final ChessPiece piece;

    public ChessSquare(ChessPosition position, ChessPiece piece) {
        if(position == null) throw new IllegalArgumentException();
        if(piece == null) throw new IllegalArgumentException();

        this.position = position;
        this.piece = piece;
    }

    public ChessPosition getPosition() {
        return position;
    }

    public ChessPiece getPiece() {
        return piece;
    }
}
