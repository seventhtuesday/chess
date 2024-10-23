package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.CreateRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {
    static GameDAO gameDAO = new GameDAO();
    static AuthDAO authDAO = new AuthDAO();
    static GameService gameS = new GameService(authDAO, gameDAO);



    @BeforeEach
    void clear() throws Exception {
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    void createGameGood() {
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