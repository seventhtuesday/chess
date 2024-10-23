package service;

import data_access.AuthDAO;
import data_access.GameDAO;
import data_access.UserDAO;

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
