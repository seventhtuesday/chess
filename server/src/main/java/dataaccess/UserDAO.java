package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDAO {
    private Connection conn;

    public UserDAO() throws Exception {
        try {
            DatabaseManager.createDatabase();
            try {
                conn = DatabaseManager.getConnection();
                String stmt = """
                        CREATE TABLE IF NOT EXISTS USERS (
                            `ID` int NOT NULL AUTO_INCREMENT,
                            `NAME` varchar(255) NOT NULL,
                            `PASSWORD` varchar(255) NOT NULL,
                            `EMAIL` varchar(255) NOT NULL,
                            PRIMARY KEY (`ID`),
                            INDEX(ID)
                        )
                        """;
                try (var s = conn.prepareStatement(stmt)) {
                    s.executeUpdate();
                }
            } catch (SQLException e) {
                throw new Exception(String.format("Database Failure: %s", e.getMessage()));
            }
        } catch (DataAccessException e) {
            throw new Exception(String.format("Database Failure: %s", e.getMessage()));
        }
    }

    //creates new user in the table
    public void createUser(UserData user) throws DataAccessException {
        try {
            var s = conn.prepareStatement("INSERT INTO USERS (NAME, PASSWORD, EMAIL) VALUE(?, ?, ?)");
            s.setString(1, user.username());
            s.setString(2, user.password());
            s.setString(3, user.email());
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {

    }

    /*
    private HashMap<String, UserData> users = new HashMap<>();

    //creates new user from given data
    public void createUser(UserData user) {
        users.put(user.username(), user);
    }

    //returns UserData of given username
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    //clears store
    public void clear() {
        users.clear();
    }
     */
}
