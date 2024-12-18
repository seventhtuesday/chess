package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.AuthRequest;

public class LogoutService {
    private AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(AuthRequest auth) throws Exception {
        try {
            //check that auth token is valid
            AuthData data = authDAO.getAuth(auth.authToken());
            if (data == null) {
                throw new Exception("unauthorized");
            }
            else {
                authDAO.deleteAuth(auth.authToken());
            }
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
