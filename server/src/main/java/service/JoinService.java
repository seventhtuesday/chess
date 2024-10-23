package service;

import data_access.AuthDAO;
import data_access.DataAccessException;
import data_access.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinRequest;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

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
                throw new Exception("bad request");
            }
            //verify authToken
            AuthData auth = authDAO.getAuth(joinRequest.authToken());
            if (auth == null) {
                throw new Exception("unauthorized");
            }
            //check for team already taken
            if((joinRequest.playerColor().equals(WHITE) && game.whiteUsername() != null) || (joinRequest.playerColor().equals(BLACK) && game.blackUsername() != null)) {
                throw new Exception("already taken");
            }
            //join the game
            if(joinRequest.playerColor().equals(WHITE)) {
                game = new GameData(joinRequest.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            }
            else if(joinRequest.playerColor().equals(BLACK)) {
                game = new GameData(joinRequest.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            }
            gameDAO.updateGame(game);
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
