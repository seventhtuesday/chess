package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
import model.CreateRequest;
import model.CreateResult;
import model.GameData;

import java.util.Random;

public class GameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateResult createGame(CreateRequest create) throws Exception {
        //verify not bad request
        if(create.authToken() == null || create.gameName() == null){
            throw new Exception("error: bad request");
        }
        //verify authToken
        if(authDAO.getAuth(create.authToken()) == null) {
            throw new Exception("error: unauthorized");
        }
        try {
            //generate random game ID
            Random generator = new Random();
            int gameID = generator.nextInt();
            //create the game
            ChessGame game = new ChessGame();
            GameData data = new GameData(gameID, null, null, create.gameName(), game);
            //add game to database
            gameDAO.createGame(data);
            return new CreateResult(gameID);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
