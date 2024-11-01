package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
                            `WHITE` varchar(255),
                            `BLACK` varchar(255),
                            `NAME` varchar(255) NOT NULL,
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

    public void createGame(GameData game) throws DataAccessException {
        try {
            var s = conn.prepareStatement("INSERT INTO GAME (ID, WHITE, BLACK, NAME, JSON)");

            s.setInt(1, game.gameID());
            s.setString(2, game.whiteUsername());
            s.setString(3, game.blackUsername());
            s.setString(4, game.gameName());

            var json = new Gson().toJson(game.game());
            s.setString(5, json);

            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        try {
            var s = conn.prepareStatement("SELECT * FROM GAME WHERE ID = " + gameID);
            s.setString(1, String.valueOf(gameID));
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                String white = rs.getString("WHITE");
                String black = rs.getString("BLACK");
                String name = rs.getString("NAME");

                String json = rs.getString("JSON");
                ChessGame game = new Gson().fromJson(json, ChessGame.class);

                return new GameData(gameID, white, black, name, game);
            }
            else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
        }
    }

    public ArrayList<GameData> getAllGames() throws DataAccessException {

    }

    public void updateGame(GameData game) throws DataAccessException {
        try {
            var s = conn.prepareStatement("UPDATE GAME SET WHITE=?, BLACK=?, NAME=?, JSON=?, WHERE ID=?");
            s.setString(1, game.whiteUsername());
            s.setString(2, game.blackUsername());
            s.setString(3, game.gameName());
            s.setString(4, new Gson().toJson(game.game()));
            s.setInt(5, game.gameID());

            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        try {
            var s = conn.prepareStatement("TRUNCATE TABLE GAME");
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
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
