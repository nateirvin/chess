import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ChessPositionExtendedTests
{
    @Test
    public void equals_AffirmativeTest()
    {
        ChessPosition x = new ChessPosition(5,2);
        ChessPosition y = new ChessPosition(5,2);

        Assertions.assertTrue(x.equals(y));
        Assertions.assertTrue(y.equals(x));
    }

    @Test
    public void equals_NegativeTest()
    {
        ChessPosition x = new ChessPosition(4,2);
        ChessPosition y = new ChessPosition(5,2);

        Assertions.assertFalse(x.equals(y));
        Assertions.assertFalse(y.equals(x));
    }

    @Test
    public void getHasCode_MatchesTest()
    {
        ChessPosition x = new ChessPosition(5,2);
        ChessPosition y = new ChessPosition(5,2);

        Assertions.assertEquals(x.hashCode(), y.hashCode());
    }

    @Test
    public void getHasCode_NotMatchesTest()
    {
        ChessPosition x = new ChessPosition(5,2);
        ChessPosition y = new ChessPosition(5,3);

        Assertions.assertNotEquals(x.hashCode(), y.hashCode());
    }

    @Test
    public void ListMatchingTest()
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