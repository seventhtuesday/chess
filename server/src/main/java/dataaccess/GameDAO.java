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
                        )
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
            var s = conn.prepareStatement("INSERT INTO GAME (ID, WHITE, BLACK, NAME, JSON) VALUES(?, ?, ?, ?, ?)");

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
            var s = conn.prepareStatement("SELECT * FROM GAME WHERE ID=?");
            s.setInt(1, gameID);
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
        try {
            ArrayList<GameData> games = new ArrayList<>();

            var s = conn.prepareStatement("SELECT * FROM GAME");
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                int gameID = rs.getInt("ID");
                String white = rs.getString("WHITE");
                String black = rs.getString("BLACK");
                String name = rs.getString("NAME");

                String json = rs.getString("JSON");
                ChessGame game = new Gson().fromJson(json, ChessGame.class);

                games.add(new GameData(gameID, white, black, name, game));
            }

            return games;
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
        }
    }

    public void updateGame(GameData game) throws DataAccessException {
        try {
            var s = conn.prepareStatement("UPDATE GAME SET WHITE=?, BLACK=?, NAME=?, JSON=? WHERE ID=?");
            s.setString(1, game.whiteUsername());
            s.setString(2, game.blackUsername());
            s.setString(3, game.gameName());
            s.setString(4, new Gson().toJson(game.game()));
            s.setInt(5, game.gameID());

            int result = s.executeUpdate();
            if(result == 0) {
                throw new DataAccessException("Database Failure: that game does not exist");
            }
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
}
