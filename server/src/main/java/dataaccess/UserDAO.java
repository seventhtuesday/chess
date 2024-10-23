package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserDAO {
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
}
