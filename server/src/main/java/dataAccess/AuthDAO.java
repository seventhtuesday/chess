package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDAO {
    private HashMap<String, AuthData> tokens = new HashMap<>();

    //Creates new AuthData from a UserData, stores the AuthData, and Returns it.
    public AuthData createAuth(UserData user) {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), user.username());
        tokens.put(auth.authToken(), auth);
        return auth;
    }

    //returns AuthData associated to a given authToken
    public AuthData getAuth(String token) throws DataAccessException {
        if (tokens.containsKey(token)) {
            return tokens.get(token);
        }
        else {
            throw new DataAccessException("Token not found");
        }
    }

    //removes given AuthData from store
    public void deleteAuth(String token) throws DataAccessException {
        if (tokens.containsKey(token)) {
            tokens.remove(token);
        }
        else {
            throw new DataAccessException("Token not found");
        }
    }

    //clears store
    public void clear() {
        tokens.clear();
    }
}
