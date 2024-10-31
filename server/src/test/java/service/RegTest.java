package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegTest {
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

    static RegService reg = new RegService(userDAO, authDAO);

    @BeforeEach
    void clear() throws DataAccessException {
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