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

    private int getStartRow() {
        if(getPieceType() == PieceType.PAWN)
        {
            if(getTeamColor() == ChessGame.TeamColor.WHITE) {
                return 2;
            } else {
                return 7;
            }
        }

        throw new RuntimeException("not implemented");
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
                        1,
                        true);
            }
            case PieceType.PAWN -> {
                ArrayList<ChessMove> moves = new ArrayList<>();

                if(getTeamColor() == ChessGame.TeamColor.WHITE) {
                    addPawnMoves(moves, board, myPosition, ChessMove.Direction.NORTH, ChessMove.Direction.NORTHWEST, ChessMove.Direction.NORTHEAST);
                }
                if(getTeamColor() == ChessGame.TeamColor.BLACK) {
                    addPawnMoves(moves, board, myPosition, ChessMove.Direction.SOUTH, ChessMove.Direction.SOUTHWEST, ChessMove.Direction.SOUTHEAST);
                }

                return moves;
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    private void addPawnMoves(ArrayList<ChessMove> moves,
                              ChessBoard board,
                              ChessPosition myPosition,
                              ChessMove.Direction progressDirection,
                              ChessMove.Direction takeDirection1,
                              ChessMove.Direction takeDirection2)
    {
        int stepsAllowed = myPosition.getRow() == getStartRow() ? 2 : 1;
        moves.addAll(getStandardMoves(board, myPosition, new ChessMove.Direction[]{progressDirection}, stepsAllowed, false));
        addPawnCaptureMove(moves, board, myPosition, takeDirection1);
        addPawnCaptureMove(moves, board, myPosition, takeDirection2);
    }

    private void addPawnCaptureMove(ArrayList<ChessMove> moves,
                                    ChessBoard board,
                                    ChessPosition myPosition,
                                    ChessMove.Direction direction)
    {
        ChessPosition neighbor = myPosition.getNeighbor(direction);
        ChessPiece piece = board.getPiece(neighbor);
        if(piece != null && isEnemy(piece)) {
            moves.add(new ChessMove(myPosition, neighbor, null));
        }
    }

    private ArrayList<ChessMove> getStandardMoves(ChessBoard board,
                                                  ChessPosition currentPosition,
                                                  ChessMove.Direction[] directions)
    {
        return getStandardMoves(board, currentPosition, directions, null, true);
    }

    private ArrayList<ChessMove> getStandardMoves(ChessBoard board,
                                                  ChessPosition currentPosition,
                                                  ChessMove.Direction[] directions,
                                                  Integer maximumSteps,
                                                  boolean allowCapture)
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
                        if(allowCapture)
                        {
                            moves.add(new ChessMove(currentPosition, possiblePosition, null));
                            stepsTaken++;
                        }
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
