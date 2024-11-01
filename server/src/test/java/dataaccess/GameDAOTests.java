package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDAOTests {
    private static GameDAO dao;

    static {
        try {
            dao = new GameDAO();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        dao.clear();
    }

    @Test
    void testGameCreateGood() {
        GameData game = new GameData(1234, null, null,
                "name", new ChessGame());

        Assertions.assertDoesNotThrow(() -> dao.createGame(game));
    }

    @Test
    void testGameCreateBad() {
        GameData game = new GameData(1234, null, null,
                null, new ChessGame());
        Assertions.assertThrows(Exception.class, () -> dao.createGame(game));
    }

    @Test
    void testGameGetGood() {
        GameData game = new GameData(1234, null, null,
                "name", new ChessGame());

        try {
            dao.createGame(game);
            Assertions.assertEquals(game, dao.getGame(game.gameID()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGameGetBad() {
        try {
            Assertions.assertNull(dao.getGame(1234));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGameUpdateGood() {
        GameData game = new GameData(1234, null, null,
                "name", new ChessGame());
        GameData game2 = new GameData(1234, "white", "black",
                "name", new ChessGame());

        try {
            dao.createGame(game);
            dao.updateGame(game2);
            Assertions.assertEquals(game2, dao.getGame(game.gameID()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGameUpdateBad() {
        GameData game = new GameData(1234, null, null,
                "name", new ChessGame());
        GameData game2 = new GameData(0, null, null,
                "name", new ChessGame());

        try {
            dao.createGame(game);
            Assertions.assertThrows(Exception.class, () -> dao.updateGame(game2));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGameList() {
        GameData game = new GameData(1234, null, null,
                "name", new ChessGame());

        Assertions.assertDoesNotThrow(() -> dao.getAllGames());
        try {
            dao.createGame(game);
            Assertions.assertDoesNotThrow(() -> dao.getAllGames());
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGameClear() {
        GameData game = new GameData(1234, null, null,
                "name", new ChessGame());

        Assertions.assertDoesNotThrow(() -> dao.clear());
        try {
            dao.createGame(game);
            Assertions.assertEquals(game, dao.getGame(game.gameID()));
            Assertions.assertDoesNotThrow(() -> dao.clear());
            Assertions.assertNull(dao.getGame(game.gameID()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}
