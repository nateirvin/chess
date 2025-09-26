package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeam;
    private ChessBoard board;

    public ChessGame()
    {
        currentTeam = TeamColor.WHITE;

        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(startPosition == null) {
            throw new IllegalArgumentException();
        }

        ChessPiece piece = board.getPiece(startPosition);

        if(piece == null) {
            return null;
        }

        return
            piece.pieceMoves(board, startPosition).stream()
                 .filter(move -> {
                    if(piece.getPieceType() == ChessPiece.PieceType.KING &&
                       thisPieceInThisPositionIsThreatened(piece, move.getEndPosition()))
                    {
                        return false;
                    }

                    if(piece.getPieceType() == ChessPiece.PieceType.PAWN && move.isDiagonal())
                    {
                        ChessPiece pieceToTake = board.getPiece(move.getEndPosition());
                        if(pieceToTake == null)
                        {
                            return false;
                        }
                    }

                    return true;
                 })
                 .toList();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(move == null) {
            throw new IllegalArgumentException();
        }

        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece == null) {
            throw new InvalidMoveException("There is no piece at the start position.");
        } else if(piece.getTeamColor() != currentTeam) {
            throw new InvalidMoveException("It is not your turn.");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(!validMoves.contains(move))
        {
            throw new InvalidMoveException();
        }

        ChessPiece otherPiece = board.getPiece(move.getEndPosition());
        if(otherPiece != null)
        {
            if(!otherPiece.isEnemy(piece)) {
                throw new InvalidMoveException("You cannot attack an ally.");
            }

            board.removePiece(move.getEndPosition());
        }

        board.updatePiecePosition(move);
        setTeamTurn(currentTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        ChessSquare kingSquare = board.square(teamColor, ChessPiece.PieceType.KING);
        return thisPieceInThisPositionIsThreatened(kingSquare.getPiece(), kingSquare.getPosition());
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessSquare kingSquare = board.square(teamColor, ChessPiece.PieceType.KING);
        ChessPiece king = kingSquare.getPiece();
        ChessSquare threat = threatForPieceInPosition(king, kingSquare.getPosition());

        if(threat != null)  //is in check
        {
            Collection<ChessMove> kingMoves = king.pieceMoves(board, kingSquare.getPosition());
            for (ChessMove kingMove : kingMoves)
            {
                ChessPosition kingDestination = kingMove.getEndPosition();

                ChessPiece threatenedByKing = board.getPiece(kingDestination);
                if(threatenedByKing == threat.getPiece())
                {
                    //king can take away
                    return false;
                }

                boolean isCheck = thisPieceInThisPositionIsThreatened(king, kingDestination);
                if (!isCheck)
                {
                    //king can move
                    return false;
                }
            }

            Collection<ChessSquare> allySquares = board.teamPieces(king.getTeamColor());
            for(ChessSquare allySquare : allySquares)
            {
                Collection<ChessMove> allyMoves = allySquare.getPiece().pieceMoves(board, allySquare.getPosition());
                for (ChessMove allyMove : allyMoves)
                {
                    if(allyMove.getEndPosition().equals(threat.getPosition()))
                    {
                        //ally can take away
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor)
    {
        return !isInCheck(teamColor) && !kingCanMove(teamColor) && !kingIsProtected(teamColor);
    }

    private boolean kingIsProtected(TeamColor teamColor)
    {
        ChessSquare king = board.square(teamColor, ChessPiece.PieceType.KING);
        Collection<ChessPosition> allNeighbors = king.getPosition().allNeighbors();
        for(ChessPosition neighbor : allNeighbors)
        {
            ChessPiece neighborPiece = board.getPiece(neighbor);
            if(neighborPiece != null && neighborPiece.getTeamColor() == teamColor)
            {
                return true;
            }
        }

        return false;
    }

    private boolean kingCanMove(TeamColor teamColor)
    {
        ChessSquare king = board.square(teamColor, ChessPiece.PieceType.KING);
        Collection<ChessMove> kingMoves = king.getPiece().pieceMoves(board, king.getPosition());

        for(ChessMove kingMove : kingMoves)
        {
            boolean isCheck = thisPieceInThisPositionIsThreatened(king.getPiece(), kingMove.getEndPosition());
            if (!isCheck)
            {
                return true;
            }
        }

        return false;
    }

    private boolean thisPieceInThisPositionIsThreatened(ChessPiece piece, ChessPosition position)
    {
        ChessSquare threatener = threatForPieceInPosition(piece, position);
        return threatener != null;
    }

    private ChessSquare threatForPieceInPosition(ChessPiece piece, ChessPosition position)
    {
        TeamColor opponentColor = piece.getTeamColor() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;

        Collection<ChessSquare> opponentSquares = board.teamPieces(opponentColor);
        for(ChessSquare opponentSquare : opponentSquares)
        {
            Collection<ChessPosition> opponentMoves = opponentSquare.getPiece().threatens(board, opponentSquare.getPosition());
            for (ChessPosition opponentMove : opponentMoves)
            {
                if(opponentMove.equals(position))
                {
                    return opponentSquare;
                }
            }
        }

        return null;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        if(board == null) {
            throw new IllegalArgumentException();
        }

        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeam == chessGame.currentTeam &&
                Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, board);
    }
}
