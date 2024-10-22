package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDAO {
    private HashMap<String, AuthData> tokens = new HashMap<>();

    //Creates new AuthData from a UserData, stores the AuthData, and Returns it.
    public AuthData createAuthData(UserData user) {
        AuthData auth = new AuthData(user.username(), UUID.randomUUID().toString());
        tokens.put(auth.username(), auth);
        return auth;
    }

    //returns AuthData associated to a given authToken
    public AuthData getAuth(String token) {
        return tokens.get(token);
    }

    //removes given AuthData from store
    public void deleteAuth(AuthData auth) {
        tokens.remove(auth.username());
    }

    //clears store
    public void clear() {
        tokens.clear();
    }
}
