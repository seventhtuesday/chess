package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
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
            throw new Exception("error: unauthorized");
        }
        try {
            ArrayList<GameData> temp = gameDAO.getAllGames();
            ArrayList<GameResult> result = new ArrayList<>();
            for (GameData game : temp) {
                result.add(new GameResult(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
            }
            return result;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
