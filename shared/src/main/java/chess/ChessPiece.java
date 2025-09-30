package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.pieceColor = pieceColor;
        this.pieceType = type;
    }

    public String shortCode()
    {
        String code;
        if(getPieceType() == PieceType.KNIGHT)
        {
           code = "N";
        }
        else
        {
            code = getPieceType().toString().toUpperCase().substring(0,1);
        }

        if(getTeamColor() == ChessGame.TeamColor.BLACK) {
            code = code.toLowerCase();
        }

        return code;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        switch (getPieceType())
        {
            case PieceType.KNIGHT -> {
                return new Knight(board, myPosition).moves();
            }
            case PieceType.PAWN -> {
                return new Pawn(board, myPosition).moves();
            }
            default -> {
                return new StandardPiece(board, myPosition).moves();
            }
        }
    }

    protected boolean isEnemy(ChessPiece otherPiece)
    {
        return this.getTeamColor() != otherPiece.getTeamColor();
    }

    public Collection<ChessPosition> threatens(ChessBoard board, ChessPosition currentPosition)
    {
        switch (getPieceType())
        {
            case PieceType.KNIGHT -> {
                return new Knight(board, currentPosition).threatens();
            }
            case PieceType.PAWN -> {
                return new Pawn(board, currentPosition).threatens();
            }
            default -> {
                return new StandardPiece(board, currentPosition).threatens();
            }
        }
    }

    @SuppressWarnings("unused")
    protected Collection<ChessPosition> threatens()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType);
    }

    @Override
    public String toString() {
        return getTeamColor() + " " + getPieceType();
    }
}
