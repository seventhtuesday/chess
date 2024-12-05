package websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;

@WebSocket
public class WebSocketHandler {
    private WebSocketService webSocketService = new WebSocketService();
    private GameService gameService;

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {}
    @OnWebSocketClose
    public void onClose(Session session) {}
    @OnWebSocketError
    public void onError(Throwable error) {}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            UserGameCommand.CommandType commandType = UserGameCommand.CommandType.valueOf(json.get("type").getAsString());
            switch (commandType) {
                case LEAVE -> leave(new Gson().fromJson(message, LeaveCommand.class));
                case RESIGN -> resign(new Gson().fromJson(message, ResignCommand.class));
                case CONNECT -> connect(new Gson().fromJson(message, ConnectCommand.class), session);
                case MAKE_MOVE -> move(new Gson().fromJson(message, MoveCommand.class));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            var error = new ErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void leave(LeaveCommand leaveCommand) {

    }

    private void resign(ResignCommand resignCommand) {

    }

    private void connect(ConnectCommand connectCommand, Session session) throws Exception {
        String user = connectCommand.getAuthToken();
        if(user == null) {
            var message = new ErrorMessage("authentication failure");
        }
        int id = connectCommand.getGameID();
        var gameData = gameService.getGame(id);
        if (gameData == null) {
            var message = new ErrorMessage("that game doesn't exist");
        }

        webSocketService.add(id, new Sesh(user, session));
    }

    private void move(MoveCommand moveCommand) {

    }

    private void sendMessage(String message, Session session) {

    }

    private void broadcast(int gameID, String message, Session exempt) {

    }
}
