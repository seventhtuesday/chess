package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinTest {
    static GameDAO gameDAO = new GameDAO();
    static AuthDAO authDAO = new AuthDAO();
    static JoinService joinS = new JoinService(authDAO, gameDAO);

    @BeforeEach
    void clear() {
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    void joinGameGood() {
        //create user
        UserData user = new UserData("good", "pass", "email@mail.com");
        //create auth for user
        AuthData auth = authDAO.createAuth(user);
        //create game
        GameData game = new GameData(1234, null, null, "test", new ChessGame());
        gameDAO.createGame(game);
        //test joining game
        Assertions.assertDoesNotThrow(() -> joinS.join(new JoinRequest("White", game.gameID(), auth.authToken())));
    }

    @Test
    void joinGameBad() {
        //create user
        UserData user = new UserData("good", "pass", "email@mail.com");
        //create auth for user
        AuthData auth = authDAO.createAuth(user);
        //create game
        GameData game = new GameData(1234, "tester", null, "test", new ChessGame());
        gameDAO.createGame(game);
        //test null input
        Assertions.assertThrows(Exception.class, () -> joinS.join(new JoinRequest(null, game.gameID(), auth.authToken())));
        //test team already taken
        Assertions.assertThrows(Exception.class, () -> joinS.join(new JoinRequest("White", game.gameID(), auth.authToken())));
    }
}