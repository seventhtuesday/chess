package service;

import DataAccess.AuthDAO;
import DataAccess.GameDAO;
import model.AuthRequest;
import model.GameData;
import model.GameResult;

import java.util.ArrayList;

public class ListService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public ListService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ArrayList<GameResult> list(AuthRequest request) throws Exception {
        //verify authToken
        if(authDAO.getAuth(request.authToken()) == null) {
            throw new Exception("unauthorized");
        }
        try {
            ArrayList<GameData> temp = gameDAO.getAllGames();
            ArrayList<GameResult> games = new ArrayList<>() {};
            for (GameData game : temp) {
                games.add(new GameResult(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
            }
            return games;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
