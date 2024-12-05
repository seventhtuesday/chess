package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import service.LoginService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotifyMessage;
import websocket.messages.ServerMessage;

@WebSocket
public class WebSocketHandler {
    private WebSocketService webSocketService = new WebSocketService();
    private GameService gameService;
    private LoginService loginService;

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
        String user = loginService.getUsername(connectCommand.getAuthToken());
        if(user == null) {
            var message = new ErrorMessage("authentication failure");
            sendMessage(message, session);
            return;
        }
        int id = connectCommand.getGameID();
        var gameData = gameService.getGame(id);
        if (gameData == null) {
            var message = new ErrorMessage("that game doesn't exist");
            sendMessage(message, session);
            return;
        }
        webSocketService.add(id, new Sesh(user, session));
        var team = connectCommand.getTeamColor();
        ServerMessage message = null;
        if(team == null) {
            message = new NotifyMessage("joined as Observer");
            sendMessage(message, session);
        }
        else if (team == ChessGame.TeamColor.WHITE) {
            message = new NotifyMessage("joined as WHITE");
            sendMessage(message, session);
            message = new NotifyMessage(user + " has joined as " + team.toString());
            broadcast(id, message, session);
        }
        else if (team == ChessGame.TeamColor.BLACK) {
            message = new NotifyMessage("joined as BLACK");
            sendMessage(message, session);
            message = new NotifyMessage(user + " has joined as " + team.toString());
            broadcast(id, message, session);
        }
        message = new LoadMessage(gameData);
        broadcast(id, message, null);
    }

    private void move(MoveCommand moveCommand) {

    }

    private void sendMessage(ServerMessage message, Session session) {
        message.
        if(session.isOpen()) {

        }
    }

    private void broadcast(int gameID, ServerMessage message, Session exempt) {

    }
}
