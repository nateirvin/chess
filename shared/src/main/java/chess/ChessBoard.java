package chess;

import java.util.HashMap;
import java.util.Map;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final Map<ChessPosition, ChessPiece> pieces;

    public ChessBoard() {
        pieces = new HashMap<>();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pieces.put(position, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return pieces.get(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        pieces.clear();

        addPiece(new ChessPosition(1,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        setupPawns(ChessGame.TeamColor.WHITE, 2);

        addPiece(new ChessPosition(8,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        setupPawns(ChessGame.TeamColor.BLACK, 7);
    }

    private void setupPawns(ChessGame.TeamColor pieceColor, int row) {
        for(int col = ChessPosition.FirstColumn; col <= ChessPosition.LastColumn; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(pieceColor, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public String toString()
    {
        StringBuilder description = new StringBuilder();
        for(int r = ChessPosition.BottomRow; r <= ChessPosition.TopRow; r++)
        {
            for(int c = ChessPosition.FirstColumn; c <= ChessPosition.LastColumn; c++)
            {
                ChessPiece piece = getPiece(new ChessPosition(r, c));
                if(piece != null)
                {
                    description.append(piece.shortCode());
                }
                else
                {
                    description.append(" ");
                }
            }
            description.append("\n");
        }
        return description.toString();
    }

    @Override
    public int hashCode()
    {
        int code = 0;
        for(int r = ChessPosition.BottomRow; r <= ChessPosition.TopRow; r++)
        {
            for(int c = ChessPosition.FirstColumn; c <= ChessPosition.LastColumn; c++)
            {
                ChessPiece piece = getPiece(new ChessPosition(r, c));
                if(piece != null)
                {
                    code += piece.hashCode();
                }
            }
        }
        return code;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null || obj.getClass() != getClass())
        {
            return false;
        }
        ChessBoard other = (ChessBoard) obj;
        if(other.pieces.size() != pieces.size())
        {
            return false;
        }
        for(int r = ChessPosition.BottomRow; r <= ChessPosition.TopRow; r++)
        {
            for(int c = ChessPosition.FirstColumn; c <= ChessPosition.LastColumn; c++)
            {
                ChessPiece piece = getPiece(new ChessPosition(r, c));
                ChessPiece otherPiece = other.getPiece(new ChessPosition(r, c));
                if(piece != null)
                {
                    if(!piece.equals(otherPiece))
                    {
                        return false;
                    }
                }
                else if(otherPiece != null)
                {
                    return false;
                }
            }
        }
        return true;
    }
}
