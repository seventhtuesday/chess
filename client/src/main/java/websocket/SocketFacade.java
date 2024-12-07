package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ui.LoopR;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotifyMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

public class SocketFacade extends Endpoint {
    private final Session session;
    private final LoopR loopR;

    public SocketFacade(String url, LoopR loop) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, socketURI);
            loopR = loop;

            session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    JsonObject json = JsonParser.parseString(s).getAsJsonObject();
                    ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.valueOf(json.get("serverMessageType").getAsString());
                    switch (type) {
                        case NOTIFICATION -> loopR.notify(new Gson().fromJson(s, NotifyMessage.class));
                        case LOAD_GAME -> loopR.load(new Gson().fromJson(s, LoadMessage.class));
                        case ERROR -> loopR.error(new Gson().fromJson(s, ErrorMessage.class));
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void connect(String auth, int gameID, ChessGame.TeamColor teamColor) {
        try {
            var cmd = new ConnectCommand(auth, gameID, teamColor);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void leave(String authToken, int gameID) {
        try {
            var cmd = new LeaveCommand(authToken, gameID);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void move(String authToken, int gameID, ChessMove move) {
        try {
            var cmd = new MoveCommand(authToken, gameID, move);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void resign(String authToken, int gameID) {
        try {
            var cmd = new ResignCommand(authToken, gameID);
            session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
