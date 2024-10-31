package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class AuthDAO {
    private final Connection conn;

    //creates table in database for AuthData
    public AuthDAO() throws Exception {
        try {
            DatabaseManager.createDatabase();
            try {
                conn = DatabaseManager.getConnection();
                String stmt = """
                        CREATE TABLE IF NOT EXISTS AUTH (
                            `NAME` varchar(255) NOT NULL,
                            `TOKEN` varchar(255) NOT NULL
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

    //creates and stores AuthData in the table
    public AuthData createAuth(UserData user) throws DataAccessException {
        try {
            var s = conn.prepareStatement("INSERT INTO AUTH (NAME, TOKEN) VALUE (?, ?)");
            String auth = UUID.randomUUID().toString();
            s.setString(1, user.username());
            s.setString(2, auth);
            s.executeUpdate();

            return new AuthData(auth, user.username());
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
        }
    }

    //gets the AuthData of the given token from the table
    public AuthData getAuth(String token) throws DataAccessException {
        try {
            var s = conn.prepareStatement("SELECT * FROM AUTH WHERE TOKEN=?");
            s.setString(1, token);
            ResultSet rs = s.executeQuery();
            String username = "";
            while (rs.next()) {
                username = rs.getString("NAME");
            }
            if(username.isEmpty()) {
                throw new DataAccessException("invalid token");
            }
            return new AuthData(token, username);
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
        }
    }

    //removes AuthData of given token from the table
    public void deleteAuth(String token) throws DataAccessException {
        try {
            var s = conn.prepareStatement("DELETE FROM AUTH WHERE TOKEN=?");
            s.setString(1, token);
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
        }
    }

    //clears AuthData table
    public void clear() throws DataAccessException {
        try {
            var s = conn.prepareStatement("TRUNCATE TABLE AUTH");
            s.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Database Failure: %s", e.getMessage()));
        }
    }


    /* Memory Methods

    private HashMap<String, AuthData> tokens = new HashMap<>();

    //Creates new AuthData from a UserData, stores the AuthData, and Returns it.
    public AuthData createAuth(UserData user) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), user.username());
        tokens.put(auth.authToken(), auth);
        return auth;
    }

    //returns AuthData associated to a given authToken
    public AuthData getAuth(String token) throws DataAccessException {
        return tokens.get(token);
    }

    //removes given AuthData from store
    public void deleteAuth(String token) throws DataAccessException {
        tokens.remove(token);
    }

    //clears store
    public void clear() {
        tokens.clear();
    }
     */

}
