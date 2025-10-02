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

    @Override
    public String toString() {
        return piece + " at " + position;
    }

    public boolean isInStartPosition() {
        switch (piece.getPieceType()) {
            case PAWN -> {
                return position.getRow() == 2 || position.getRow() == 7;
            }
            case KING -> {
                return position.getColumn() == 5 && isIsTopOrBottomRow();
            }
            case ROOK -> {
                return isIsTopOrBottomRow() &&
                        (position.getColumn() ==
                                ChessPosition.FirstColumn || position.getColumn() == ChessPosition.LastColumn);
            }
            default -> throw new RuntimeException("not implemented");
        }
    }

    private boolean isIsTopOrBottomRow() {
        return position.getRow() == ChessPosition.TopRow || position.getRow() == ChessPosition.BottomRow;
    }
}
