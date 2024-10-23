package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RegService;

public class LoginTest {
    static UserDAO userDAO = new UserDAO();
    static AuthDAO authDAO = new AuthDAO();
    static LoginService loginS = new LoginService(userDAO, authDAO);

    @BeforeEach
    void clear() throws Exception {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void testLoginGood() {
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