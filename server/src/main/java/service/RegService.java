package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

public class RegService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData registerUser(UserData newUser) throws Exception {

    }
}
