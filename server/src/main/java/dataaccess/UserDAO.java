package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.ResultSet;
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
                            `NAME` varchar(255) NOT NULL,
                            `PASSWORD` varchar(255) NOT NULL,
                            `EMAIL` varchar(255) NOT NULL
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
        try {
            var s = conn.prepareStatement("SELECT PASSWORD, EMAIL FROM USERS WHERE NAME=?");
            s.setString(1, username);
            ResultSet rs = s.executeQuery();
            String hashedPassword = "";
            String email = "";
            while (rs.next()) {
                hashedPassword = rs.getString("PASSWORD");
                email = rs.getString("EMAIL");
            }
            if (hashedPassword.isEmpty() && email.isEmpty()) {
                return null;
            }
            return new UserData(username, hashedPassword, email);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        try {
            var s = conn.prepareStatement("TRUNCATE TABLE USERS");
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
