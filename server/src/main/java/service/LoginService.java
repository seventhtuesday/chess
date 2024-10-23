package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.LoginRequest;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

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
                throw new Exception("error: unauthorized");
            }
            //check if password matches
            if (user.password().equals(data.password())) {
                authDAO.deleteAuth(data.username());
                return authDAO.createAuth(data);
            } else {
                throw new Exception("error: unauthorized");
            }
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
