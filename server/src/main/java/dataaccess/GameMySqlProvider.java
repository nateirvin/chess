package dataaccess;

import model.GameData;
import model.UpsertGameResult;
import service.AlreadyTakenException;
import java.sql.*;
import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class GameMySqlProvider implements GameDataAccess
{
    public GameMySqlProvider() throws DataAccessException
    {
        DatabaseManager.createDatabase();

        String statement =
                """     
                CREATE TABLE IF NOT EXISTS `games` (
                  `game_id` int unsigned NOT NULL AUTO_INCREMENT,
                  `game_name` varchar(100) NOT NULL,
                  `white_user_id` int unsigned DEFAULT NULL,
                  `black_user_id` int unsigned DEFAULT NULL,
                  `game_data` json NOT NULL,
                  PRIMARY KEY (`game_id`),
                  UNIQUE KEY `games_unique` (`game_name`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
                """;

        DatabaseManager.execute(statement, "failed to create table 'games'");
    }

    @Override
    public UpsertGameResult findOrCreateGame(String name)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(
                    """
                        INSERT IGNORE INTO games
                          (game_name, game_data)
                        VALUES
                          (?, '{}')
                        """, RETURN_GENERATED_KEYS))
            {
                ps.setString(1, name);

                ps.executeUpdate();

                Integer gameId = DatabaseManager.getIdentity(ps);
                if(gameId != null)
                {
                    return new UpsertGameResult(new GameData(gameId, name), true);
                }
            }

            return getGame(conn, null, name);
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<GameData> getAllGames()
    {
        ArrayList<GameData> list = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection())
        {
            try (PreparedStatement ps = conn.prepareStatement(
                    """
                        SELECT
                            game_id,
                            game_name,
                            u1.username AS white_user_name,
                            u2.username AS black_user_name
                        FROM games
                            LEFT JOIN users AS u1
                                ON games.white_user_id = u1.user_id
                            LEFT JOIN users AS u2
                                ON games.black_user_id = u2.user_id
                        """))
            {
                try(var rs = ps.executeQuery()) {
                    while (rs.next())
                    {
                        GameData gameData = readGameData(rs);
                        list.add(gameData);
                    }
                }
            }
        } catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }

        return list;
    }

    private static GameData readGameData(ResultSet rs) throws SQLException {
        return new GameData(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4));
    }

    @Override
    public boolean setWhiteTeam(int gamedID, String username)
    {
        String commandText = """
                    UPDATE games
                    SET white_user_id = (SELECT user_id FROM users WHERE username = ?)
                    WHERE game_id = ?
                        AND
                        (
                            white_user_id IS NULL
                            OR
                            white_user_id = (SELECT user_id FROM users WHERE username = ?)
                        )
                    """;

        return setPlayer(gamedID, username, commandText);
    }

    @Override
    public boolean setBlackTeam(int gamedID, String username)
    {
        String commandText = """
                    UPDATE games
                    SET black_user_id = (SELECT user_id FROM users WHERE username = ?)
                    WHERE game_id = ?
                        AND
                        (
                            black_user_id IS NULL
                            OR
                            black_user_id = (SELECT user_id FROM users WHERE username = ?)
                        )
                    """;

        return setPlayer(gamedID, username, commandText);
    }

    private static boolean setPlayer(int gamedID, String username, String commandText)
    {
        try (Connection conn = DatabaseManager.getConnection())
        {
            if(getGame(conn, gamedID, null) == null) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(commandText))
            {
                ps.setInt(2, gamedID);
                ps.setString(1, username);
                ps.setString(3, username);

                int rowsAffected = ps.executeUpdate();

                if(rowsAffected == 0) {
                    throw new AlreadyTakenException("username", username);
                }
            }
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }

        return true;
    }

    private static UpsertGameResult getGame(Connection conn, Integer gameId, String name) throws SQLException
    {
        try (PreparedStatement ps = conn.prepareStatement(
                """
                    SELECT
                        game_id,
                        game_name,
                        u1.username AS white_user_name,
                        u2.username AS black_user_name
                    FROM games
                        LEFT JOIN users AS u1
                            ON games.white_user_id = u1.user_id
                        LEFT JOIN users AS u2
                            ON games.black_user_id = u2.user_id
                    WHERE game_name = ?
                        OR game_id = ?
                    """))
        {
            if(gameId == null) {
                ps.setString(1, name);
                ps.setNull(2, Types.NULL);
            } else {
                ps.setNull(1, Types.NULL);
                ps.setInt(2, gameId);
            }

            try(var rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    GameData gameData = readGameData(rs);
                    return new UpsertGameResult(gameData, false);
                }
            }
        }

        return null;
    }

    @Override
    public void deleteAllGames() {
        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM games")){
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
