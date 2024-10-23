package service;

import DataAccess.AuthDAO;
import DataAccess.GameDAO;
import DataAccess.UserDAO;

public class ClearService {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() throws Exception {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }
}
