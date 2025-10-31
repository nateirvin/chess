package dataaccess;

import model.UpsertGameResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class GameMySqlProviderTests
{
    private GameMySqlProvider classUnderTest;

    @BeforeEach
    public void setup() throws DataAccessException
    {
        this.classUnderTest = new GameMySqlProvider();
        TestHelper.resetDatabase();
    }

    @Test
    public void findOrCreateGameCreatesGameIfItDoesNotExist() throws DataAccessException, SQLException
    {
        UpsertGameResult actual = classUnderTest.findOrCreateGame("game_forty");

        Assertions.assertEquals("game_forty", actual.gameName());
        Assertions.assertTrue(actual.isNew());
        Assertions.assertNull(actual.whiteUsername());
        Assertions.assertNull(actual.blackUsername());
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_id FROM games WHERE game_name = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, "game_forty");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Assertions.assertEquals(rs.getInt(1), actual.gameID());
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @CsvSource({
            ",",
            "user_1,",
            ",user_1",
            "user_2,",
            ",user_2",
            "user_1,user_2",
            "user_2,user_1"})
    public void findOrCreateGameReturnsGameIfDoesExist(String whiteUser, String blackUser) throws SQLException, DataAccessException
    {
        int userId1 = TestHelper.insertTestUser("user_1");
        int userId2 = TestHelper.insertTestUser("user_2");
        Integer whiteUserId = null;
        Integer blackUserId = null;
        if(whiteUser != null && whiteUser.equals("user_1"))
        {
            whiteUserId = userId1;
        }
        else if(whiteUser != null && whiteUser.equals("user_2"))
        {
            whiteUserId = userId2;
        }
        if(blackUser != null && blackUser.equals("user_1"))
        {
            blackUserId = userId1;
        }
        else if(blackUser != null && blackUser.equals("user_2"))
        {
            blackUserId = userId2;
        }

        int gameId;
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(
                    """
                        INSERT INTO games
                          (game_name, white_user_id, black_user_id, game_data)
                        VALUES (?, ?, ?, '{}')
                        """, RETURN_GENERATED_KEYS))
            {
                ps.setString(1, "game_forty5");
                if(whiteUserId == null) {
                    ps.setNull(2, Types.NULL);
                } else {
                    ps.setInt(2, whiteUserId);
                }
                if(blackUserId == null) {
                    ps.setNull(3, Types.NULL);
                } else {
                    ps.setInt(3, blackUserId);
                }

                ps.executeUpdate();

                gameId = DatabaseManager.getIdentity(ps);
            }
        }

        UpsertGameResult actual = classUnderTest.findOrCreateGame("game_forty5");

        Assertions.assertEquals(gameId, actual.gameID());
        Assertions.assertEquals("game_forty5", actual.gameName());
        Assertions.assertFalse(actual.isNew());
        Assertions.assertEquals(whiteUser, actual.whiteUsername());
        Assertions.assertEquals(blackUser, actual.blackUsername());
    }
}
