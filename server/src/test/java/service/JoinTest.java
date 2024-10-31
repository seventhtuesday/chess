package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinTest {
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

    static JoinService joinS = new JoinService(authDAO, gameDAO);

    @BeforeEach
    void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    void joinGameGood() throws DataAccessException {
        //create user
        UserData user = new UserData("good", "pass", "email@mail.com");
        //create auth for user
        AuthData auth = authDAO.createAuth(user);
        //create game
        GameData game = new GameData(1234, null, null, "test", new ChessGame());
        gameDAO.createGame(game);
        //test joining game
        Assertions.assertDoesNotThrow(() -> joinS.join(new JoinRequest(ChessGame.TeamColor.WHITE, game.gameID(), auth.authToken())));
    }

    @Test
    void joinGameBad() throws DataAccessException {
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
        Assertions.assertThrows(Exception.class, () -> joinS.join(new JoinRequest(ChessGame.TeamColor.WHITE, game.gameID(), auth.authToken())));
    }
}