package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
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
        try {
            //check for valid request
            if (newUser.username() == null || newUser.password() == null || newUser.email() == null) {
                throw new Exception("bad request");
            }
            //check username
            if(userDAO.getUser(newUser.username()) != null) {
                throw new Exception("already taken");
            }
            else{
                //add user
                userDAO.createUser(newUser);
                //create & return auth
                return authDAO.createAuth(newUser);
            }

        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
