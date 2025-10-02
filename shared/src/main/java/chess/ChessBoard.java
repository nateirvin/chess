package chess;

import java.util.*;
import java.util.function.Predicate;

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

    public ChessBoard(ChessBoard source) {
        this.pieces = new HashMap<>(source.pieces);
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

    public Collection<ChessSquare> teamPieces(ChessGame.TeamColor teamColor)
    {
        return findPieces(piece -> piece.getTeamColor() == teamColor);
    }

    public ChessSquare kingFor(ChessGame.TeamColor teamColor)
    {
        return findPieces(piece -> piece.getTeamColor() == teamColor &&
                                              piece.getPieceType() == ChessPiece.PieceType.KING)
                .getFirst();
    }

    private List<ChessSquare> findPieces(Predicate<ChessPiece> predicate)
    {
        return pieces
                .entrySet().stream()
                .filter(entry -> {
                    ChessPiece piece = entry.getValue();
                    return predicate.test(piece);
                })
                .map(entry -> new ChessSquare(entry.getKey(), entry.getValue()))
                .toList();
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor)
    {
        ChessSquare kingSquare = kingFor(teamColor);
        return !threatsForPieceInPosition(kingSquare.getPiece(), kingSquare.getPosition()).isEmpty();
    }

    private boolean thisPieceInThisPositionIsThreatened(ChessPiece piece, ChessPosition position)
    {
        return !threatsForPieceInPosition(piece, position).isEmpty();
    }

    ArrayList<ChessSquare> threatsForPieceInPosition(ChessPiece piece, ChessPosition position)
    {
        ArrayList<ChessSquare> threats = new ArrayList<>();

        ChessGame.TeamColor opponentColor =
                piece.getTeamColor() == ChessGame.TeamColor.WHITE
                        ? ChessGame.TeamColor.BLACK
                        : ChessGame.TeamColor.WHITE;

        Collection<ChessSquare> opponentSquares = teamPieces(opponentColor);
        for(ChessSquare opponentSquare : opponentSquares)
        {
            ChessPiece opponentPiece = opponentSquare.getPiece();
            ChessPosition opponentPosition = opponentSquare.getPosition();
            Collection<ChessPosition> opponentMoves = opponentPiece.threatens(this, opponentPosition);
            for (ChessPosition opponentMove : opponentMoves)
            {
                if(opponentMove.equals(position))
                {
                    threats.add(opponentSquare);
                }
            }
        }

        return threats;
    }

    public boolean isInCheckmate(ChessGame.TeamColor teamColor)
    {
        if(!isInCheck(teamColor))
        {
            return false;
        }

        for(ChessSquare teamSquare : teamPieces(teamColor))
        {
            Collection<ChessMove> moves = teamSquare.getPiece().pieceMoves(this, teamSquare.getPosition());
            for (ChessMove move : moves)
            {
                if (canMakeMove(teamColor, move))
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean canMakeMove(ChessGame.TeamColor teamColor, ChessMove move)
    {
        ChessBoard potentialBoard = new ChessBoard(this);
        potentialBoard.makeMove(move);
        return !potentialBoard.isInCheck(teamColor);
    }

    public boolean isInStalemate(ChessGame.TeamColor teamColor)
    {
        return !isInCheck(teamColor) && !kingCanMove(teamColor) && !kingIsProtected(teamColor);
    }

    private boolean kingIsProtected(ChessGame.TeamColor teamColor)
    {
        ChessSquare kingSquare = kingFor(teamColor);
        ChessPiece kingPiece = kingSquare.getPiece();
        Collection<ChessPosition> allNeighbors = kingSquare.getPosition().allNeighbors();
        for(ChessPosition neighbor : allNeighbors)
        {
            ChessPiece neighborPiece = getPiece(neighbor);
            if(neighborPiece != null) {
                if (!kingPiece.isEnemy(neighborPiece)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean kingCanMove(ChessGame.TeamColor teamColor)
    {
        ChessSquare kingSquare = kingFor(teamColor);
        ChessPiece king = kingSquare.getPiece();
        ChessPosition kingPosition = kingSquare.getPosition();

        for(ChessMove kingMove : king.pieceMoves(this, kingPosition))
        {
            ChessPosition potentialDestination = kingMove.getEndPosition();
            if (!thisPieceInThisPositionIsThreatened(king, potentialDestination))
            {
                return true;
            }
        }

        return false;
    }

    void makeMove(ChessMove move)
    {
        assert move != null;
        pieces.remove(move.getEndPosition());

        ChessPiece movedPiece = pieces.remove(move.getStartPosition());
        assert movedPiece != null;

        if(move.getPromotionPiece() != null)
        {
            pieces.put(move.getEndPosition(),
                       new ChessPiece(movedPiece.getTeamColor(), move.getPromotionPiece()));
        }
        else
        {
            pieces.put(move.getEndPosition(), movedPiece);
        }
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
        for(int r = ChessPosition.TopRow; r >= ChessPosition.BottomRow; r--)
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
                ChessPosition position = new ChessPosition(r, c);
                ChessPiece piece = getPiece(position);
                if(piece != null)
                {
                    code += position.hashCode() * piece.hashCode();
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
