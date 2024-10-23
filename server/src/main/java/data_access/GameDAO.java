package data_access;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class GameDAO {
    private HashMap<Number, GameData> list = new HashMap<>();

    //creates new game of given data in store
    public void createGame(GameData game) {
        list.put(game.gameID(), game);
    }

    //returns game of given ID from store
    public GameData getGame(int gameID) throws DataAccessException {
        return list.get(gameID);
    }

    //returns all games
    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> games = new ArrayList<>(list.values());
        return games;
    }

    public void updateGame(GameData game) throws DataAccessException {
        if (list.containsKey(game.gameID())) {
            list.put(game.gameID(), game);
        }
        else {
            throw new DataAccessException("No such game");
        }
    }

    //clears store
    public void clear() {
        list.clear();
    }
}
