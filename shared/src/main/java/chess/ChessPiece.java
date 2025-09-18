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
                return getStandardMoves(board, myPosition,
                        new ChessMove.Direction[] {
                                ChessMove.Direction.NORTHWEST,
                                ChessMove.Direction.NORTHEAST,
                                ChessMove.Direction.SOUTHWEST,
                                ChessMove.Direction.SOUTHEAST
                });
            }
            case PieceType.ROOK -> {
                return getStandardMoves(board, myPosition,
                        new ChessMove.Direction[] {
                                ChessMove.Direction.NORTH,
                                ChessMove.Direction.SOUTH,
                                ChessMove.Direction.EAST,
                                ChessMove.Direction.WEST
                        });
            }
            case PieceType.QUEEN -> {
                return getStandardMoves(board, myPosition,
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
            case PieceType.KING -> {
                return getStandardMoves(board, myPosition,
                        new ChessMove.Direction[] {
                                ChessMove.Direction.NORTH,
                                ChessMove.Direction.SOUTH,
                                ChessMove.Direction.EAST,
                                ChessMove.Direction.WEST,
                                ChessMove.Direction.NORTHWEST,
                                ChessMove.Direction.NORTHEAST,
                                ChessMove.Direction.SOUTHWEST,
                                ChessMove.Direction.SOUTHEAST
                        },
                        1);
            }
            case PieceType.PAWN -> {
                if(getTeamColor() == ChessGame.TeamColor.WHITE)
                {
                    return pawnMoves(board, myPosition, ChessMove.Direction.NORTH, ChessMove.Direction.NORTHWEST, ChessMove.Direction.NORTHEAST, 2);
                }
                else if(getTeamColor() == ChessGame.TeamColor.BLACK)
                {
                    return pawnMoves(board, myPosition, ChessMove.Direction.SOUTH, ChessMove.Direction.SOUTHWEST, ChessMove.Direction.SOUTHEAST, 7);
                }
                throw new UnsupportedOperationException();
            }
            case PieceType.KNIGHT -> {
                ArrayList<ChessMove> moves = new ArrayList<>();

                ArrayList<ChessPosition> possibilities = new ArrayList<>();
                possibilities.add(myPosition.getNeighbor(ChessMove.Direction.NORTH, ChessMove.Direction.NORTH, ChessMove.Direction.EAST));
                possibilities.add(myPosition.getNeighbor(ChessMove.Direction.NORTH, ChessMove.Direction.NORTH, ChessMove.Direction.WEST));
                possibilities.add(myPosition.getNeighbor(ChessMove.Direction.SOUTH, ChessMove.Direction.SOUTH, ChessMove.Direction.EAST));
                possibilities.add(myPosition.getNeighbor(ChessMove.Direction.SOUTH, ChessMove.Direction.SOUTH, ChessMove.Direction.WEST));
                possibilities.add(myPosition.getNeighbor(ChessMove.Direction.WEST, ChessMove.Direction.WEST, ChessMove.Direction.NORTH));
                possibilities.add(myPosition.getNeighbor(ChessMove.Direction.WEST, ChessMove.Direction.WEST, ChessMove.Direction.SOUTH));
                possibilities.add(myPosition.getNeighbor(ChessMove.Direction.EAST, ChessMove.Direction.EAST, ChessMove.Direction.NORTH));
                possibilities.add(myPosition.getNeighbor(ChessMove.Direction.EAST, ChessMove.Direction.EAST, ChessMove.Direction.SOUTH));

                for (ChessPosition potential : possibilities)
                {
                    if(potential != null)
                    {
                        var piece = board.getPiece(potential);
                        if(piece == null || isEnemy(piece))
                        {
                            moves.add(new ChessMove(myPosition, potential, null));
                        }
                    }
                }

                return moves;
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    private ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessMove.Direction mainDirection, ChessMove.Direction takeDirection1, ChessMove.Direction takeDirection2, int initialRow)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();

        moves.addAll(
            pawnMoves(board, myPosition, mainDirection, false));

        if(myPosition.getRow() == initialRow)
        {
            if(board.getPiece(myPosition.getNeighbor(mainDirection)) == null)
            {
                ChessPosition potentialMove = myPosition.getNeighbor(mainDirection, mainDirection);
                if(board.getPiece(potentialMove) == null)
                {
                    moves.add(new ChessMove(myPosition, potentialMove, null));
                }
            }
        }

        moves.addAll(
            pawnMoves(board, myPosition, takeDirection1, true));

        moves.addAll(
            pawnMoves(board, myPosition, takeDirection2, true));

        return moves;
    }

    private ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, ChessMove.Direction direction, boolean isCapture)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPosition potentialMove = myPosition.getNeighbor(direction);
        if(potentialMove != null)
        {
            ChessPiece blocker = board.getPiece(potentialMove);
            if(isCapture && blocker != null && this.isEnemy(blocker)
               ||
               !isCapture && blocker == null)
            {
                if(potentialMove.getRow() == ChessPosition.BottomRow || potentialMove.getRow() == ChessPosition.TopRow)
                {
                    moves.add(new ChessMove(myPosition, potentialMove, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, potentialMove, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, potentialMove, PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, potentialMove, PieceType.BISHOP));
                }
                else
                {
                    moves.add(new ChessMove(myPosition, potentialMove, null));
                }
            }
        }

        return moves;
    }

    private ArrayList<ChessMove> getStandardMoves(ChessBoard board,
                                                  ChessPosition currentPosition,
                                                  ChessMove.Direction[] directions)
    {
        return getStandardMoves(board, currentPosition, directions, null);
    }

    private ArrayList<ChessMove> getStandardMoves(ChessBoard board,
                                                  ChessPosition currentPosition,
                                                  ChessMove.Direction[] directions,
                                                  Integer maximumSteps)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();

        for(ChessMove.Direction direction : directions)
        {
            int stepsTaken = 0;
            ChessPosition possiblePosition = currentPosition;

            while(possiblePosition != null)
            {
                possiblePosition = possiblePosition.getNeighbor(direction);
                if(possiblePosition != null)  //not at an edge
                {
                    ChessPiece pieceAtPosition = board.getPiece(possiblePosition);

                    if(pieceAtPosition == null) //no piece in this spot
                    {
                        moves.add(new ChessMove(currentPosition, possiblePosition, null));
                        stepsTaken++;
                        if(maximumSteps != null && stepsTaken == maximumSteps)
                        {
                            possiblePosition = null;
                        }
                    }
                    else if(isEnemy(pieceAtPosition))  //piece in spot is enemy
                    {
                        moves.add(new ChessMove(currentPosition, possiblePosition, null));
                        stepsTaken++;
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

    private boolean isEnemy(ChessPiece otherPiece) {
        return otherPiece.getTeamColor() != getTeamColor();
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
