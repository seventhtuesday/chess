package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.LoginRequest;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData login(LoginRequest user) throws Exception {
        try {
            //check if user exists
            UserData data = userDAO.getUser(user.username());
            if (data == null) {
                throw new Exception("unauthorized");
            }
            //check if password matches
            if (BCrypt.checkpw(user.password(), data.password())) {
                    authDAO.deleteAuth(data.username());
                return authDAO.createAuth(data);
            } else {
                throw new Exception("unauthorized");
            }
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
