package dataaccess;

import model.GameData;
import model.UpsertGameResult;
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
                        """))
            {
                ps.setString(1, name);

                try(var rs = ps.executeQuery()) {
                    if(rs.next())
                    {
                        GameData gameData = new GameData(
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(4));
                        return new UpsertGameResult(gameData, false);
                    }
                }
            }

            return null;
        }
        catch (SQLException | DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<GameData> getAllGames() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean setWhiteTeam(int gamedID, String username) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean setBlackTeam(int gamedID, String username) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void deleteAllGames() {
        throw new RuntimeException("not implemented");
    }
}
