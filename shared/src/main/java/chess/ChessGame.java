package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
    private final HashMap<TeamColor, ArrayList<ChessPiece>> captures;

    public ChessGame()
    {
        currentTeam = TeamColor.WHITE;

        board = new ChessBoard();
        board.resetBoard();

        captures = new HashMap<>();
        captures.put(TeamColor.WHITE, new ArrayList<>());
        captures.put(TeamColor.BLACK, new ArrayList<>());
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

        //TODO: filter invalid moves, including Check
        return piece.pieceMoves(board, startPosition);
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

        //TODO: check is move if valid

        ChessPiece otherPiece = board.getPiece(move.getEndPosition());
        if(otherPiece != null)
        {
            //TODO: redundant?
            if(!otherPiece.isEnemy(piece)) {
                throw new InvalidMoveException("You cannot attack an ally.");
            }

            ChessPiece removedPiece = board.removePiece(move.getEndPosition());
            assert otherPiece == removedPiece;
            captures.get(currentTeam).add(removedPiece);
        }

        board.updatePiecePosition(move);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        ChessPosition kingPosition = board.positionOf(teamColor, ChessPiece.PieceType.KING);

        TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;

        for(ChessSquare opponent : board.getTeamPieces(opponentColor))
        {
            Collection<ChessMove> moves = opponent.getPiece().pieceMoves(board, opponent.getPosition());
            for(ChessMove move : moves)
            {
                if(move.getEndPosition().equals(kingPosition))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
        if (!isInCheck(teamColor))
        {
            ChessSquare square = board.square(teamColor, ChessPiece.PieceType.KING);
            Collection<ChessMove> moves = validMoves(square.getPosition());
            if (moves.isEmpty())
            {
                return true;
            }
        }

        return false;
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

        //TODO: should this reset the team?
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
                Objects.equals(board, chessGame.board) &&
                Objects.equals(captures, chessGame.captures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, board, captures);
    }
}
