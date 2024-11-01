package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
                //encrypt password
                String hashPass = BCrypt.hashpw(newUser.password(), BCrypt.gensalt());
                UserData user = new UserData(newUser.username(), hashPass, newUser.email());
                //add user
                userDAO.createUser(user);
                //create & return auth
                return authDAO.createAuth(user);
            }

        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
