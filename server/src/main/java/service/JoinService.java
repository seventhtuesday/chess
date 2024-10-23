package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinRequest;

public class JoinService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public JoinService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void join(JoinRequest joinRequest) throws Exception {
        try {
            GameData game = gameDAO.getGame(joinRequest.gameID());
            //check for bad input
            if (joinRequest.authToken() == null || joinRequest.playerColor() == null || game == null) {
                throw new Exception("error: bad request");
            }
            //verify authToken
            AuthData auth = authDAO.getAuth(joinRequest.authToken());
            if (auth == null) {
                throw new Exception("error: unauthorized");
            }
            //check for team already taken
            if((joinRequest.playerColor().equals("White") && game.whiteUsername() != null) || (joinRequest.playerColor().equals("Black") && game.blackUsername() != null)) {
                throw new Exception("error: already taken");
            }
            //join the game
            if(joinRequest.playerColor().equals("White")) {
                game = new GameData(joinRequest.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            }
            else if(joinRequest.playerColor().equals("Black")) {
                game = new GameData(joinRequest.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            }
            gameDAO.updateGame(game);
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
