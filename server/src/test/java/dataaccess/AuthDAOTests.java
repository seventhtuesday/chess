package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {
    private static AuthDAO dao;

    static {
        try {
            dao = new AuthDAO();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        dao.clear();
    }

    @Test
    void testCreateAuthGood() {
        UserData user = new UserData("test", "password", "email@email.com");
        Assertions.assertDoesNotThrow(() -> dao.createAuth(user));
    }

    @Test
    void testCreateAuthBad() {
        UserData user = new UserData(null, "password", "email@email.com");
        Assertions.assertThrows(Exception.class, () -> dao.createAuth(user));
    }

    @Test
    void testGetAuthGood() {
        UserData user = new UserData("test", "password", "email@email.com");
        try {
            AuthData auth = dao.createAuth(user);
            Assertions.assertEquals(auth, dao.getAuth(auth.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetAuthBad() {
        UserData user = new UserData("test", "password", "email@email.com");
        try {
            dao.createAuth(user);
            Assertions.assertNull(dao.getAuth("wrong token"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testDeleteAuthGood() {
        UserData user = new UserData("test", "password", "email@email.com");
        try {
            AuthData auth = dao.createAuth(user);
            Assertions.assertDoesNotThrow(() -> dao.deleteAuth(auth.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testDeleteAuthBad() {
        try {
            Assertions.assertFalse(dao.deleteAuth("wrong token"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testClearAuth() {
        UserData user = new UserData("test", "password", "email@email.com");
        try {
            AuthData auth = dao.createAuth(user);
            Assertions.assertEquals(auth, dao.getAuth(auth.authToken()));
            Assertions.assertDoesNotThrow(dao::clear);
            Assertions.assertNull(dao.getAuth(auth.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}
