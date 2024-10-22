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
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    //returns all games
    public ArrayList<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    //clears store
    public clear() {
        games.clear();
    }
}
