package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ListTest {
    static GameDAO gameDAO = new GameDAO();
    static AuthDAO authDAO = new AuthDAO();
    static ListService listS = new ListService(authDAO, gameDAO);

    @BeforeEach
    void clear() {
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    void listGameGood() {
        //create user
        UserData user = new UserData("good", "pass", "email@mail.com");
        //create auth for user
        AuthData auth = authDAO.createAuth(user);
        //create games
        GameData game = new GameData(1234, null, null, "test", new ChessGame());
        GameData game2 = new GameData(5678, null, null, "test2", new ChessGame());
        gameDAO.createGame(game);
        gameDAO.createGame(game2);
        ArrayList<GameResult> start = new ArrayList<>();
        start.add(new GameResult(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        start.add(new GameResult(game2.gameID(), game.whiteUsername(), game2.blackUsername(), game2.gameName()));
        //test listing
        try {
            var req = new AuthRequest(auth.authToken());
            ArrayList<GameResult> result = listS.list(req);
            if(!result.equals(start)) {
                throw new Exception();
            }
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void listGameEmpty() {
        //create user
        UserData user = new UserData("good", "pass", "email@mail.com");
        //create auth for user
        AuthData auth = authDAO.createAuth(user);
        //test that will return empty set
        try {
            var req = new AuthRequest(auth.authToken());
            Assertions.assertTrue(listS.list(req).isEmpty());
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}