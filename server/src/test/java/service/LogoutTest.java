package service;

import data_access.AuthDAO;
import model.AuthData;
import model.AuthRequest;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogoutTest {
    static AuthDAO authDAO = new AuthDAO();
    static LogoutService logoutS = new LogoutService(authDAO);

    @BeforeEach
    void clear() throws Exception {
        authDAO.clear();
    }

    @Test
    void testLogoutGood() {
        try {
            //create user
            UserData user = new UserData("good", "pass", "email@mail.com");
            //create auth for user
            AuthData data = authDAO.createAuth(user);
            AuthRequest logout = new AuthRequest(data.authToken());
            //test logout
            logoutS.logout(logout);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void testLogoutBad() {
        //test that a session that doesn't exist will error
        Assertions.assertThrows(Exception.class, () -> logoutS.logout(new AuthRequest("1234")));
    }
}