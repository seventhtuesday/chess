package dataAccess;

import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class GameDAO {
    private HashMap<Number, GameData> games = new HashMap<>();

    //creates new game of given data in store
    public void createGame(GameData game) {
        games.put(game.gameID(), game);
    }

    //returns game of given ID from store
    public GameData getGame(int gameID) throws DataAccessException {
        if (games.containsKey(gameID)) {
            return games.get(gameID);
        }
        else {
            throw new DataAccessException("No such game");
        }
    }

    //returns all games
    public ArrayList<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())) {
            games.put(game.gameID(), game);
        }
        else {
            throw new DataAccessException("No such game");
        }
    }

    //clears store
    public void clear() {
        games.clear();
    }
}
