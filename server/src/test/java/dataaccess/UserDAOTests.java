package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserDAOTests {
    private static UserDAO dao;

    static {
        try {
            dao = new UserDAO();
        } catch (Exception e) {
                throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void clear() throws DataAccessException {
        dao.clear();
    }

    @Test
    void testUserCreateGood() {
        Assertions.assertDoesNotThrow(() -> dao.createUser(
                new UserData("test", "password", "email@email.com")));
    }

    @Test
    void testUserCreateBad() {
        Assertions.assertThrows(DataAccessException.class, () -> dao.createUser(
                new UserData(null, "password", "email@email.com")));
    }

    @Test
    void testUserGetGood() {
        try {
            var user = new UserData("test", "password", "email@email.com");
            dao.createUser(user);
            Assertions.assertEquals(user, dao.getUser("test"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testUserGetBad() {
        try {
            Assertions.assertNull(dao.getUser("bad name"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testUserClear() {
        Assertions.assertDoesNotThrow(dao::clear);
        try {
            UserData user = new UserData("test", "password", "email@email.com");
            dao.createUser(user);
            Assertions.assertEquals(user, dao.getUser("test"));
            Assertions.assertDoesNotThrow(dao::clear);
            Assertions.assertNull(dao.getUser("test"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}
