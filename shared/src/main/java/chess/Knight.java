package chess;

import java.util.ArrayList;

class Knight extends ChessPiece
{
    private final ChessBoard board;
    private final ChessPosition startPosition;

    public Knight(ChessBoard board, ChessPosition startPosition)
    {
        super(board.getPiece(startPosition).getTeamColor(), board.getPiece(startPosition).getPieceType());
        this.board = board;
        this.startPosition = startPosition;
    }

    public ArrayList<ChessMove> moves()
    {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ArrayList<ChessPosition> possibilities = new ArrayList<>();
        possibilities.add(startPosition.getNeighbor(ChessMove.Direction.NORTH, ChessMove.Direction.NORTH, ChessMove.Direction.EAST));
        possibilities.add(startPosition.getNeighbor(ChessMove.Direction.NORTH, ChessMove.Direction.NORTH, ChessMove.Direction.WEST));
        possibilities.add(startPosition.getNeighbor(ChessMove.Direction.SOUTH, ChessMove.Direction.SOUTH, ChessMove.Direction.EAST));
        possibilities.add(startPosition.getNeighbor(ChessMove.Direction.SOUTH, ChessMove.Direction.SOUTH, ChessMove.Direction.WEST));
        possibilities.add(startPosition.getNeighbor(ChessMove.Direction.WEST, ChessMove.Direction.WEST, ChessMove.Direction.NORTH));
        possibilities.add(startPosition.getNeighbor(ChessMove.Direction.WEST, ChessMove.Direction.WEST, ChessMove.Direction.SOUTH));
        possibilities.add(startPosition.getNeighbor(ChessMove.Direction.EAST, ChessMove.Direction.EAST, ChessMove.Direction.NORTH));
        possibilities.add(startPosition.getNeighbor(ChessMove.Direction.EAST, ChessMove.Direction.EAST, ChessMove.Direction.SOUTH));

        for (ChessPosition potential : possibilities)
        {
            if(potential != null)
            {
                ChessPiece otherPiece = board.getPiece(potential);
                if(otherPiece == null || isEnemy(otherPiece))
                {
                    moves.add(new ChessMove(startPosition, potential, null));
                }
            }
        }

        return moves;
    }
}
