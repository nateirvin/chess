package chess;

import java.util.ArrayList;
import java.util.Collection;

public class StandardPiece extends ChessPiece
{
    private final ChessBoard board;
    private final ChessPosition startPosition;

    public StandardPiece(ChessBoard board, ChessPosition startPosition)
    {
        super(board.getPiece(startPosition).getTeamColor(), board.getPiece(startPosition).getPieceType());

        this.board = board;
        this.startPosition = startPosition;
    }

    @Override
    public Collection<ChessPosition> threatens()
    {
        return pieceMoves(board, startPosition).stream()
                .map(ChessMove::getEndPosition)
                .toList();
    }

    ArrayList<ChessMove> moves()
    {
        switch (getPieceType())
        {
            case PieceType.BISHOP -> {
                return moves(
                        new ChessMove.Direction[] {
                                ChessMove.Direction.NORTHWEST,
                                ChessMove.Direction.NORTHEAST,
                                ChessMove.Direction.SOUTHWEST,
                                ChessMove.Direction.SOUTHEAST
                        });
            }
            case PieceType.ROOK -> {
                return moves(
                        new ChessMove.Direction[] {
                                ChessMove.Direction.NORTH,
                                ChessMove.Direction.SOUTH,
                                ChessMove.Direction.EAST,
                                ChessMove.Direction.WEST
                        });
            }
            case PieceType.QUEEN, PieceType.KING -> {
                return moves(
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
            default -> throw new UnsupportedOperationException();
        }
    }

    private ArrayList<ChessMove> moves(ChessMove.Direction[] directions)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();

        for(ChessMove.Direction direction : directions)
        {
            ChessPosition possiblePosition = startPosition;

            while(possiblePosition != null)
            {
                possiblePosition = possiblePosition.neighbor(direction);
                if(possiblePosition != null)  //not at an edge
                {
                    ChessPiece pieceAtPosition = board.getPiece(possiblePosition);

                    if(pieceAtPosition == null) //no piece in this spot
                    {
                        moves.add(new ChessMove(startPosition, possiblePosition, null));
                        if(getPieceType() == PieceType.KING)
                        {
                            possiblePosition = null;
                        }
                    }
                    else if(isEnemy(pieceAtPosition))  //piece in spot is enemy
                    {
                        moves.add(new ChessMove(startPosition, possiblePosition, null));
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
}
