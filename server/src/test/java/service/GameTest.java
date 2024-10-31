package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.CreateRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {
    static GameDAO gameDAO;
    static AuthDAO authDAO;

    static {
        try {
            gameDAO = new GameDAO();
            authDAO = new AuthDAO();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static GameService gameS = new GameService(authDAO, gameDAO);



    @BeforeEach
    void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    void createGameGood() throws DataAccessException {
        //create user
        UserData user = new UserData("good", "pass", "email@mail.com");
        //create auth for user
        AuthData data = authDAO.createAuth(user);
        //test create game
        CreateRequest create = new CreateRequest("test", data.authToken());
        Assertions.assertDoesNotThrow(() -> gameS.createGame(create));
    }

    @Test
    void createGameBad() {
        CreateRequest create = new CreateRequest(null,null);
        Assertions.assertThrows(Exception.class, () -> gameS.createGame(create));
    }
}