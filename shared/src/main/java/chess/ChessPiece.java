package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        switch (getPieceType()){
            case PieceType.BISHOP -> {
                ChessMove.Direction [] directions = {
                        ChessMove.Direction.NORTHWEST,
                        ChessMove.Direction.NORTHEAST,
                        ChessMove.Direction.SOUTHWEST,
                        ChessMove.Direction.SOUTHEAST
                };
                for(ChessMove.Direction direction : directions)
                {
                    ChessPosition possiblePosition = myPosition;
                    while(possiblePosition != null)
                    {
                        possiblePosition = possiblePosition.getNeighbor(direction);
                        if(possiblePosition != null)  //not at an edge
                        {
                            ChessPiece pieceAtPosition = board.getPiece(possiblePosition);

                            if(pieceAtPosition == null) //no piece in this spot
                            {
                                moves.add(new ChessMove(myPosition, possiblePosition, null));
                            }
                            else if(pieceAtPosition.getTeamColor() != getTeamColor())  //piece in spot is enemy
                            {
                                moves.add(new ChessMove(myPosition, possiblePosition, null));
                                possiblePosition = null;
                            }
                            else //piece in spot is ally
                            {
                                possiblePosition = null;
                            }
                        }
                    }
                }
            }
            default -> throw new UnsupportedOperationException();
        }

        return moves;
    }

    @Override
    public String toString() {
        return getTeamColor() + " " + getPieceType();
    }
}
