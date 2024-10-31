package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearTest {

    @Test
    void testClear() {
        try {
            UserDAO userDAO = new UserDAO();
            AuthDAO authDAO = new AuthDAO();
            GameDAO gameDAO = new GameDAO();
            ClearService clearS = new ClearService(userDAO, authDAO, gameDAO);

            //create user
            UserData user = new UserData("good", "pass", "email@mail.com");
            userDAO.createUser(user);
            //create auth for user
            AuthData auth = authDAO.createAuth(user);
            //create games
            GameData game = new GameData(1234, null, null, "test", new ChessGame());
            GameData game2 = new GameData(5678, null, null, "test2", new ChessGame());
            gameDAO.createGame(game);
            gameDAO.createGame(game2);
            //test data is there
            Assertions.assertSame(user, userDAO.getUser(user.username()));
            Assertions.assertSame(auth, authDAO.getAuth(auth.authToken()));
            Assertions.assertFalse(gameDAO.getAllGames().isEmpty());
            //test clear
            Assertions.assertDoesNotThrow(clearS::clear);
            //test data is gone
            Assertions.assertNull(userDAO.getUser(user.username()));
            Assertions.assertNull(authDAO.getAuth(auth.authToken()));
            Assertions.assertTrue(gameDAO.getAllGames().isEmpty());
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }
}