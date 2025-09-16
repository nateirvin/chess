package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {
        if(row < 1 || row > 8) throw new IllegalArgumentException("invalid row");
        if(col < 1 || col > 8) throw new IllegalArgumentException("invalid column");

        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public ChessPosition getNeighbor(ChessMove.Direction direction) {
        switch (direction)
        {
            case ChessMove.Direction.NORTHWEST -> {
                if(row == 8 || col == 1) {
                    return null;
                }
                return new ChessPosition(row + 1, col - 1);
            }
            case ChessMove.Direction.NORTHEAST -> {
                if(row == 8 || col == 8){
                    return null;
                }
                return new ChessPosition(row + 1, col + 1);
            }
            case ChessMove.Direction.SOUTHWEST -> {
                if(row == 1 || col == 1){
                    return null;
                }
                return new ChessPosition(row - 1, col - 1);
            }
            case ChessMove.Direction.SOUTHEAST -> {
                if(row == 1 || col == 8){
                    return null;
                }
                return new ChessPosition(row - 1, col + 1);
            }
            default -> throw new RuntimeException("not implemented");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
