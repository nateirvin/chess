package chess;

import java.util.ArrayList;

class Pawn
{
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final ChessPiece self;
    private final int initialRow;

    public Pawn(ChessPiece self, ChessBoard board, ChessPosition startPosition)
    {
        this.board = board;
        this.startPosition = startPosition;
        this.self = self;
        this.initialRow =
                self.getTeamColor() == ChessGame.TeamColor.WHITE
                        ? ChessPosition.BottomRow + 1
                        : ChessPosition.TopRow - 1;
    }

    public ArrayList<ChessMove> moves()
    {
        if(self.getTeamColor() == ChessGame.TeamColor.WHITE)
        {
            return movesInDirections(ChessMove.Direction.NORTH, ChessMove.Direction.NORTHWEST, ChessMove.Direction.NORTHEAST);
        }
        else if(self.getTeamColor() == ChessGame.TeamColor.BLACK)
        {
            return movesInDirections(ChessMove.Direction.SOUTH, ChessMove.Direction.SOUTHWEST, ChessMove.Direction.SOUTHEAST);
        }
        throw new UnsupportedOperationException();
    }

    private ArrayList<ChessMove> movesInDirections(ChessMove.Direction mainDirection, ChessMove.Direction takeDirection1, ChessMove.Direction takeDirection2)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();

        moves.addAll(initialMoves(mainDirection));
        moves.addAll(movesFor(mainDirection, false));
        moves.addAll(movesFor(takeDirection1, true));
        moves.addAll(movesFor(takeDirection2, true));

        return moves;
    }

    private ArrayList<ChessMove> initialMoves(ChessMove.Direction direction)
    {
        ArrayList<ChessMove> move = new ArrayList<>();

        if(startPosition.getRow() == initialRow)
        {
            if(board.getPiece(startPosition.getNeighbor(direction)) == null)
            {
                ChessPosition potentialMove = startPosition.getNeighbor(direction, direction);
                if(board.getPiece(potentialMove) == null)
                {
                    move.add(new ChessMove(startPosition, potentialMove, null));
                }
            }
        }

        return move;
    }

    private ArrayList<ChessMove> movesFor(ChessMove.Direction direction, boolean isCapture)
    {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPosition potentialMove = startPosition.getNeighbor(direction);
        if(potentialMove != null)
        {
            ChessPiece blocker = board.getPiece(potentialMove);
            if(isCapture && blocker != null && ChessPiece.areEnemies(self, blocker)
                    ||
                    !isCapture && blocker == null)
            {
                if(potentialMove.getRow() == ChessPosition.BottomRow ||
                   potentialMove.getRow() == ChessPosition.TopRow)
                {
                    moves.add(new ChessMove(startPosition, potentialMove, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(startPosition, potentialMove, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(startPosition, potentialMove, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(startPosition, potentialMove, ChessPiece.PieceType.BISHOP));
                }
                else
                {
                    moves.add(new ChessMove(startPosition, potentialMove, null));
                }
            }
        }

        return moves;
    }
}
