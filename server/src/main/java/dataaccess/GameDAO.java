package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
public class GameDAO {
    private Connection conn;

    public GameDAO() throws Exception {
        try {
            DatabaseManager.createDatabase();
            try {
                conn = DatabaseManager.getConnection();
                String stmt = """
                        CREATE TABLE IF NOT EXISTS GAME (
                            `ID` int NOT NULL,
                            `WHITENAME` varchar(255),
                            `BLACKNAME` varchar(255),
                            `GAMENAME` varchar(255) NOT NULL,
                            `JSON` TEXT NOT NULL
                        """;
                try (var s = conn.prepareStatement(stmt)) {
                    s.executeUpdate();
                }
            } catch (SQLException e) {
                throw new Exception(String.format("Database Failure: %s", e.getMessage()));
            }
        } catch (DataAccessException e) {
            throw new Exception(String.format("Database Failure: %s", e.getMessage()));
        }
    }

/*
public class GameDAO {
    private HashMap<Number, GameData> list = new HashMap<>();

    //creates new game of given data in store
    public void createGame(GameData game) {
        list.put(game.gameID(), game);
    }

    //returns game of given ID from store
    public GameData getGame(int gameID) throws DataAccessException {
        return list.get(gameID);
    }

    //returns all games
    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> games = new ArrayList<>(list.values());
        return games;
    }

    public void updateGame(GameData game) throws DataAccessException {
        if (list.containsKey(game.gameID())) {
            list.put(game.gameID(), game);
        }
        else {
            throw new DataAccessException("No such game");
        }
    }

    //clears store
    public void clear() {
        list.clear();
    }
 */

}
