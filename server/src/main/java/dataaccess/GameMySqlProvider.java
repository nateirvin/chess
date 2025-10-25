package dataaccess;

import model.GameData;
import model.UpsertGameResult;

import java.util.ArrayList;

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
    public UpsertGameResult findOrCreateGame(String name) {
        throw new RuntimeException("not implemented");
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
