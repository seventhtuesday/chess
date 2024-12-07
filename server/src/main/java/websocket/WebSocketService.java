package websocket;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketService {
    public ConcurrentHashMap<Integer, Set<Sesh>> sessions = new ConcurrentHashMap<>();

    public void add(int gameID, Sesh session) {
        if (!sessions.containsKey(gameID)) {
            sessions.put(gameID, new HashSet<>());
        }
        sessions.get(gameID).add(session);
    }

    public void remove(int gameID, Sesh session) {
        sessions.get(gameID).remove(session);
    }

    public void removeSession(Sesh session) {
        for (Set<Sesh> sessionSet : sessions.values()) {
            sessionSet.remove(session);
        }
    }

    public Set<Sesh> getSessions(int gameID) {
        return sessions.get(gameID);
    }
}