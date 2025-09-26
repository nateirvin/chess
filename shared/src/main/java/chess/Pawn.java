package chess;

import java.util.ArrayList;

class Pawn extends ChessPiece
{
    private final ChessBoard board;
    private final ChessPosition startPosition;
    private final int initialRow;

    public Pawn(ChessBoard board, ChessPosition startPosition)
    {
        super(board.getPiece(startPosition).getTeamColor(), board.getPiece(startPosition).getPieceType());

        this.board = board;
        this.startPosition = startPosition;
        this.initialRow =
                getTeamColor() == ChessGame.TeamColor.WHITE
                        ? ChessPosition.BottomRow + 1
                        : ChessPosition.TopRow - 1;
    }

    public ArrayList<ChessMove> moves()
    {
        if(getTeamColor() == ChessGame.TeamColor.WHITE)
        {
            return movesInDirections(ChessMove.Direction.NORTH, ChessMove.Direction.NORTHWEST, ChessMove.Direction.NORTHEAST);
        }
        else if(getTeamColor() == ChessGame.TeamColor.BLACK)
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
            if(board.getPiece(startPosition.neighbor(direction)) == null)
            {
                ChessPosition potentialMove = startPosition.neighbor(direction, direction);
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

        ChessPosition potentialMove = startPosition.neighbor(direction);
        if(potentialMove != null)
        {
            ChessPiece blocker = board.getPiece(potentialMove);
            if(isCapture && blocker != null && isEnemy(blocker)
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
