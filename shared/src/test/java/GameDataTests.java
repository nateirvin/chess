import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameDataTests
{
    @Test
    public void resignationStates()
    {
        var classUnderTest = new GameData(0, "testgame", "joe", "bob");
        ChessGame game = new ChessGame();
        classUnderTest.setGame(game);

        classUnderTest.concededBy("joe");

        Assertions.assertTrue(classUnderTest.isOver());
        Assertions.assertEquals("bob", classUnderTest.getWinnerUsername());
    }
}
