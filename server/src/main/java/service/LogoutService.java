package service;

import data_access.AuthDAO;
import data_access.DataAccessException;
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
                authDAO.deleteAuth(data.authToken());
            }
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
}
