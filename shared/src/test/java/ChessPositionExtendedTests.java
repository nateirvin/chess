import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ChessPositionExtendedTests
{
    @Test
    public void equalsAffirmativeTest()
    {
        ChessPosition x = new ChessPosition(5,2);
        ChessPosition y = new ChessPosition(5,2);

        Assertions.assertEquals(x, y);
        Assertions.assertEquals(y, x);
    }

    @Test
    public void equalsNegativeTest()
    {
        ChessPosition x = new ChessPosition(4,2);
        ChessPosition y = new ChessPosition(5,2);

        Assertions.assertNotEquals(x, y);
        Assertions.assertNotEquals(y, x);
    }

    @Test
    public void getHasCodeMatchesTest()
    {
        ChessPosition x = new ChessPosition(5,2);
        ChessPosition y = new ChessPosition(5,2);

        Assertions.assertEquals(x.hashCode(), y.hashCode());
    }

    @Test
    public void getHasCodeNotMatchesTest()
    {
        ChessPosition x = new ChessPosition(5,2);
        ChessPosition y = new ChessPosition(5,3);

        Assertions.assertNotEquals(x.hashCode(), y.hashCode());
    }

    @Test
    public void listMatchingTest()
    {
        ArrayList<ChessPosition> a = new ArrayList<>();
        a.add(new ChessPosition(1,1));
        a.add(new ChessPosition(2,1));
        a.add(new ChessPosition(1,3));

        ArrayList<ChessPosition> b = new ArrayList<>();
        b.add(new ChessPosition(1,1));
        b.add(new ChessPosition(2,1));
        b.add(new ChessPosition(1,3));

        Assertions.assertEquals(a,b);
    }
}