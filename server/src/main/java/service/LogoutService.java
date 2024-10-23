package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;

public class LogoutService {
    private AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(AuthData auth) throws Exception {
        try {
            //check that auth token is valid
            AuthData data = authDAO.getAuth(auth.authToken());
            if (data == null) {
                throw new Exception("error: unauthorized");
            }
            else {
                authDAO.deleteAuth(data.authToken());
            }
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
