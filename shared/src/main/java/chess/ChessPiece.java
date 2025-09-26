package chess;

import java.util.ArrayList;
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
        if(getPieceType() == PieceType.KING)
        {
           code = "G";
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
            case PieceType.BISHOP -> {
                return moves(board, myPosition,
                        new ChessMove.Direction[] {
                                ChessMove.Direction.NORTHWEST,
                                ChessMove.Direction.NORTHEAST,
                                ChessMove.Direction.SOUTHWEST,
                                ChessMove.Direction.SOUTHEAST
                });
            }
            case PieceType.ROOK -> {
                return moves(board, myPosition,
                        new ChessMove.Direction[] {
                                ChessMove.Direction.NORTH,
                                ChessMove.Direction.SOUTH,
                                ChessMove.Direction.EAST,
                                ChessMove.Direction.WEST
                        });
            }
            case PieceType.QUEEN, PieceType.KING -> {
                return moves(board, myPosition,
                        new ChessMove.Direction[] {
                                ChessMove.Direction.NORTH,
                                ChessMove.Direction.SOUTH,
                                ChessMove.Direction.EAST,
                                ChessMove.Direction.WEST,
                                ChessMove.Direction.NORTHWEST,
                                ChessMove.Direction.NORTHEAST,
                                ChessMove.Direction.SOUTHWEST,
                                ChessMove.Direction.SOUTHEAST
                        });
            }
            case PieceType.KNIGHT -> {
                return new Knight(board, myPosition).moves();
            }
            case PieceType.PAWN -> {
                return new Pawn(board, myPosition).moves();
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    private ArrayList<ChessMove> moves(ChessBoard board,
                                       ChessPosition currentPosition,
                                       ChessMove.Direction[] directions)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();

        for(ChessMove.Direction direction : directions)
        {
            ChessPosition possiblePosition = currentPosition;

            while(possiblePosition != null)
            {
                possiblePosition = possiblePosition.neighbor(direction);
                if(possiblePosition != null)  //not at an edge
                {
                    ChessPiece pieceAtPosition = board.getPiece(possiblePosition);

                    if(pieceAtPosition == null) //no piece in this spot
                    {
                        moves.add(new ChessMove(currentPosition, possiblePosition, null));
                        if(getPieceType() == PieceType.KING)
                        {
                            possiblePosition = null;
                        }
                    }
                    else if(isEnemy(pieceAtPosition))  //piece in spot is enemy
                    {
                        moves.add(new ChessMove(currentPosition, possiblePosition, null));
                        possiblePosition = null;
                    }
                    else //piece in spot is ally
                    {
                        possiblePosition = null;
                    }
                }
            }
        }

        return moves;
    }

    protected boolean isEnemy(ChessPiece otherPiece)
    {
        return this.getTeamColor() != otherPiece.getTeamColor();
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
