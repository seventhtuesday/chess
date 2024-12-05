package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.CreateRequest;
import model.CreateResult;
import model.GameData;

import java.util.Random;

public class GameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateResult createGame(CreateRequest create) throws Exception {
        //verify not bad request
        if(create.authToken() == null || create.gameName() == null){
            throw new Exception("bad request");
        }
        //verify authToken
        if(authDAO.getAuth(create.authToken()) == null) {
            throw new Exception("unauthorized");
        }
        try {
            //generate random game ID
            Random generator = new Random();
            int gameID = generator.nextInt(10000);
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

    public GameData getGame(int gameID) throws Exception {
        return gameDAO.getGame(gameID);
    }
}
