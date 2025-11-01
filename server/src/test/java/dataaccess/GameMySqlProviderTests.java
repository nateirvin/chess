package dataaccess;

import model.GameData;
import model.UpsertGameResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import service.AlreadyTakenException;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class GameMySqlProviderTests
{
    private GameMySqlProvider classUnderTest;

    @BeforeEach
    public void setup() throws DataAccessException
    {
        this.classUnderTest = new GameMySqlProvider();
        TestHelper.ensureDatabaseSetup();
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
        int gameId = insertGame("game_forty5", whiteUserId, blackUserId);

        UpsertGameResult actual = classUnderTest.findOrCreateGame("game_forty5");

        Assertions.assertEquals(gameId, actual.gameID());
        Assertions.assertEquals("game_forty5", actual.gameName());
        Assertions.assertFalse(actual.isNew());
        Assertions.assertEquals(whiteUser, actual.whiteUsername());
        Assertions.assertEquals(blackUser, actual.blackUsername());
    }

    @Test
    public void getAllGamesReturnsNothingIfNoGamesInTheDatabase()
    {
        ArrayList<GameData> actual = classUnderTest.getAllGames();

        Assertions.assertEquals(0, actual.size());
    }

    @Test
    public void getAllGamesReturnsEveryGameInTheDatabase() throws SQLException, DataAccessException
    {
        insertGame(UUID.randomUUID().toString());
        insertGame(UUID.randomUUID().toString());
        insertGame(UUID.randomUUID().toString());
        insertGame(UUID.randomUUID().toString());

        ArrayList<GameData> actual = classUnderTest.getAllGames();

        Assertions.assertEquals(4, actual.size());
        Assertions.assertEquals(4, actual.stream().map(GameData::gameID).distinct().count());
        actual.forEach(game -> {
            Assertions.assertNotEquals(0, game.gameID());
            Assertions.assertFalse(game.gameName().isEmpty());
        });
    }

    @Test
    public void deleteAllGamesDeletesAllGames() throws SQLException, DataAccessException
    {
        insertGame(UUID.randomUUID().toString());
        insertGame(UUID.randomUUID().toString());
        insertGame(UUID.randomUUID().toString());

        classUnderTest.deleteAllGames();

        Assertions.assertEquals(0, classUnderTest.getAllGames().size());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    public void setWhiteTeamReturnsFalseAndMakesNoUpdatesIfGameDoesNotExist(boolean userAlreadySet) throws SQLException, DataAccessException
    {
        String username = "lois_lane";
        int userId = TestHelper.insertTestUser(username);
        int gameId = insertGame("my_test_thingie", userAlreadySet ? userId : null, null);

        boolean actual = classUnderTest.setWhiteTeam(gameId*-1, username);

        Assertions.assertFalse(actual);
        GameData actualGame = getGame(gameId);
        if(userAlreadySet) {
            Assertions.assertEquals(username, actualGame.whiteUsername());
        } else {
            Assertions.assertNull(actualGame.whiteUsername());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    public void setBlackTeamReturnsFalseAndMakesNoUpdatesIfGameDoesNotExist(boolean userAlreadySet) throws SQLException, DataAccessException
    {
        String username = "lois_lane8";
        int userId = TestHelper.insertTestUser(username);
        int gameId = insertGame("my_test_thingie4", null, userAlreadySet ? userId : null);

        boolean actual = classUnderTest.setBlackTeam(gameId*-1, username);

        Assertions.assertFalse(actual);
        GameData actualGame = getGame(gameId);
        if(userAlreadySet) {
            Assertions.assertEquals(username, actualGame.blackUsername());
        } else {
            Assertions.assertNull(actualGame.blackUsername());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    public void setWhiteTeamUpdatesRecordIfNoCurrentValue(boolean hasBlackUserName) throws SQLException, DataAccessException
    {
        String userA = "supperman";
        String userB = "lincoln";
        Integer blackUserId = null;
        if(hasBlackUserName)
        {
            blackUserId = TestHelper.insertTestUser(userB);
        }
        int gameId = insertGame("my_test_thingie", null, blackUserId);
        TestHelper.insertTestUser(userA);

        boolean actual = classUnderTest.setWhiteTeam(gameId, userA);

        Assertions.assertTrue(actual);
        GameData actualGame = getGame(gameId);
        Assertions.assertEquals(userA, actualGame.whiteUsername());
        if(hasBlackUserName) {
            Assertions.assertEquals(userB, actualGame.blackUsername());
        }
        else {
            Assertions.assertNull(actualGame.blackUsername());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true,false})
    public void setBlackTeamUpdatesRecordIfNoCurrentValue(boolean hasWhiteUserName) throws SQLException, DataAccessException
    {
        String userA = "supperman";
        String userB = "lincoln";
        Integer whiteUserId = null;
        if(hasWhiteUserName)
        {
            whiteUserId = TestHelper.insertTestUser(userB);
        }
        int gameId = insertGame("my_test_thingie_q", whiteUserId, null);
        TestHelper.insertTestUser(userA);

        boolean actual = classUnderTest.setBlackTeam(gameId, userA);

        Assertions.assertTrue(actual);
        GameData actualGame = getGame(gameId);
        Assertions.assertEquals(userA, actualGame.blackUsername());
        if(hasWhiteUserName) {
            Assertions.assertEquals(userB, actualGame.whiteUsername());
        }
        else {
            Assertions.assertNull(actualGame.whiteUsername());
        }
    }

    @Test
    public void setWhiteTeamThrowsExceptionIfValidGameButWhiteUserAlreadySet() throws SQLException, DataAccessException
    {
        String userA = "skyler";
        String userB = "plantaginut";
        int userAId = TestHelper.insertTestUser(userA);
        TestHelper.insertTestUser(userB);
        int gameId = insertGame("my_test_thingie", userAId, null);

        try{
            classUnderTest.setWhiteTeam(gameId, userB);
            Assertions.fail("should have thrown an exception");
        } catch(AlreadyTakenException actualEx) {
            Assertions.assertEquals("The username '"+userB+"' is already in use.",
                    actualEx.getMessage());
        }
    }

    @Test
    public void setBlackTeamThrowsExceptionIfValidGameButBlackUserAlreadySet() throws SQLException, DataAccessException
    {
        String userA = "skyler";
        String userB = "plantaginut";
        int userAId = TestHelper.insertTestUser(userA);
        TestHelper.insertTestUser(userB);
        int gameId = insertGame("my_test_thingie_z", null, userAId);

        try{
            classUnderTest.setBlackTeam(gameId, userB);
            Assertions.fail("should have thrown an exception");
        } catch(AlreadyTakenException actualEx) {
            Assertions.assertEquals("The username '"+userB+"' is already in use.",
                    actualEx.getMessage());
        }
    }

    @Test
    public void setWhiteTeamDoesNothingIfUserAlreadySetToThatValue() throws SQLException, DataAccessException
    {
        String userA = "skyler";
        int userAId = TestHelper.insertTestUser(userA);
        int gameId = insertGame("my_test_thingie", userAId, null);

        boolean actual = classUnderTest.setWhiteTeam(gameId, userA);

        Assertions.assertTrue(actual);
        GameData actualGame = getGame(gameId);
        Assertions.assertEquals(userA, actualGame.whiteUsername());
    }

    @Test
    public void setBlackTeamDoesNothingIfUserAlreadySetToThatValue() throws SQLException, DataAccessException
    {
        String userA = "skyler";
        int userAId = TestHelper.insertTestUser(userA);
        int gameId = insertGame("my_test_thingie", null, userAId);

        boolean actual = classUnderTest.setBlackTeam(gameId, userA);

        Assertions.assertTrue(actual);
        GameData actualGame = getGame(gameId);
        Assertions.assertEquals(userA, actualGame.blackUsername());
    }

    @NotNull
    private GameData getGame(int gameId) {
        return classUnderTest.getAllGames().stream()
                            .filter(g -> g.gameID() == gameId)
                            .findFirst().get();
    }

    private static int insertGame(String gameName) throws SQLException, DataAccessException {
        return insertGame(gameName, null, null);
    }

    private static int insertGame(String gameName, Integer whiteUserId, Integer blackUserId) throws SQLException, DataAccessException
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(
                    """
                        INSERT INTO games
                          (game_name, white_user_id, black_user_id, game_data)
                        VALUES (?, ?, ?, '{}')
                        """, RETURN_GENERATED_KEYS))
            {
                ps.setString(1, gameName);
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

                return DatabaseManager.getIdentity(ps);
            }
        }
    }
}
