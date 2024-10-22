package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegTest {
    static UserDAO userDAO = new UserDAO();
    static AuthDAO authDAO = new AuthDAO();
    static RegService reg = new RegService(userDAO, authDAO);

    @BeforeEach
    void clear() throws Exception {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void testRegGood() {
        UserData request = new UserData("Name", "Pass", "email@mail.com");
        //test that good request will register
        Assertions.assertDoesNotThrow(() -> reg.registerUser(request));
        //test that it will not allow double register
        Assertions.assertThrows(Exception.class, () -> reg.registerUser(request));
    }

    @Test
    void testRegBad() {
        UserData request = new UserData(null, "Pass", "email@mail.com");
        //test that bad request will not register
        Assertions.assertThrows(Exception.class, () -> reg.registerUser(request));
    }
}