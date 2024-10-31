package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTest {
    static UserDAO userDAO;
    static AuthDAO authDAO;

    static {
        try {
            userDAO = new UserDAO();
            authDAO = new AuthDAO();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static LoginService loginS = new LoginService(userDAO, authDAO);

    @BeforeEach
    void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void testLoginGood() throws DataAccessException {
        //create user
        UserData user = new UserData("good","pass", "email@mail.com");
        userDAO.createUser(user);
        //test that user can log in
        var req = new LoginRequest("good","pass");
        Assertions.assertDoesNotThrow(() -> loginS.login(req));
    }

    @Test
    void testLoginBad() {
        //test that a user that doesn't exist will error
        Assertions.assertThrows(Exception.class, () -> loginS.login(new LoginRequest("bad", "pass")));
    }
}