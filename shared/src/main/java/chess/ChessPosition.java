package chess;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Collection;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public static final int TOP_ROW = 8;
    public static final int BOTTOM_ROW = 1;
    public static final int FIRST_COLUMN = 1;
    public static final int LAST_COLUMN = 8;

    public ChessPosition(int row, int col) {
        if(row < BOTTOM_ROW || row > TOP_ROW) {
            throw new IllegalArgumentException("invalid row");
        }
        if(col < FIRST_COLUMN || col > LAST_COLUMN) {
            throw new IllegalArgumentException("invalid column");
        }

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

    public Collection<ChessPosition> allNeighbors() {
        ArrayList<ChessPosition> neighbors = new ArrayList<>();
        addToNeighbors(neighbors, ChessMove.Direction.NORTH);
        addToNeighbors(neighbors, ChessMove.Direction.SOUTH);
        addToNeighbors(neighbors, ChessMove.Direction.EAST);
        addToNeighbors(neighbors, ChessMove.Direction.WEST);
        addToNeighbors(neighbors, ChessMove.Direction.NORTHEAST);
        addToNeighbors(neighbors, ChessMove.Direction.NORTHWEST);
        addToNeighbors(neighbors, ChessMove.Direction.SOUTHEAST);
        addToNeighbors(neighbors, ChessMove.Direction.SOUTHWEST);
        return neighbors;
    }

    private void addToNeighbors(ArrayList<ChessPosition> neighbors, ChessMove.Direction direction) {
        ChessPosition neighbor = neighbor(direction);
        if(neighbor != null) {
            neighbors.add(neighbor);
        }
    }

    public ChessPosition neighbor(ChessMove.Direction... directions)
    {
        if(directions.length == 0)
        {
            throw new IllegalArgumentException();
        }

        ChessPosition position = this;
        for(ChessMove.Direction direction : directions)
        {
            ChessPosition newPosition = position.neighbor(direction);
            if(newPosition == null)
            {
                return null;
            }
            else
            {
                position = newPosition;
            }
        }
        return position;
    }

    public ChessPosition neighbor(ChessMove.Direction direction)
    {
        switch (direction)
        {
            case ChessMove.Direction.NORTH -> {
                if(row == TOP_ROW) {
                    return null;
                }
                return new ChessPosition(row + 1, col);
            }
            case ChessMove.Direction.SOUTH -> {
                if(row == BOTTOM_ROW) {
                    return null;
                }
                return new ChessPosition(row - 1, col);
            }
            case ChessMove.Direction.EAST -> {
                if(col == FIRST_COLUMN) {
                    return null;
                }
                return new ChessPosition(row, col - 1);
            }
            case ChessMove.Direction.WEST -> {
                if(col == LAST_COLUMN) {
                    return null;
                }
                return new ChessPosition(row, col + 1);
            }
            case ChessMove.Direction.NORTHWEST -> {
                if(row == TOP_ROW || col == FIRST_COLUMN) {
                    return null;
                }
                return new ChessPosition(row + 1, col - 1);
            }
            case ChessMove.Direction.NORTHEAST -> {
                if(row == TOP_ROW || col == LAST_COLUMN){
                    return null;
                }
                return new ChessPosition(row + 1, col + 1);
            }
            case ChessMove.Direction.SOUTHWEST -> {
                if(row == BOTTOM_ROW || col == 1){
                    return null;
                }
                return new ChessPosition(row - 1, col - 1);
            }
            case ChessMove.Direction.SOUTHEAST -> {
                if(row == BOTTOM_ROW || col == LAST_COLUMN){
                    return null;
                }
                return new ChessPosition(row - 1, col + 1);
            }
            default -> throw new UnsupportedOperationException("not a valid direction");
        }
    }

    public String toStandardNotation() {
        return "%s%d".formatted(BoardColumn.numberToLetter(col), row);
    }

    @SuppressWarnings("unused")
    public boolean equals(int row, int col) {
        return row == this.row && col == this.col;
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
        return "(%d,%d)".formatted(row, col);
    }
}
