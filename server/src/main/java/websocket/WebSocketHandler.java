package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import service.LoginService;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotifyMessage;
import websocket.messages.ServerMessage;

import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final WebSocketService webSocketService = new WebSocketService();
    private final GameService gameService;
    private final LoginService loginService;

    public WebSocketHandler(GameService gameService, LoginService loginService) {
        this.gameService = gameService;
        this.loginService = loginService;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            UserGameCommand.CommandType commandType = UserGameCommand.CommandType.valueOf(json.get("commandType").getAsString());
            switch (commandType) {
                case LEAVE -> leave(new Gson().fromJson(message, LeaveCommand.class), session);
                case RESIGN -> resign(new Gson().fromJson(message, ResignCommand.class));
                case CONNECT -> connect(new Gson().fromJson(message, ConnectCommand.class), session);
                case MAKE_MOVE -> move(new Gson().fromJson(message, MoveCommand.class), session);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            var error = new ErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void leave(LeaveCommand leaveCommand, Session session) throws Exception {
        String user = loginService.getUsername(leaveCommand.getAuthToken());
        var game = gameService.getGame(leaveCommand.getGameID());
        if (Objects.equals(user, game.whiteUsername())) {
            game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            gameService.updateGame(game);
        }
        else if (Objects.equals(user, game.blackUsername())) {
            game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            gameService.updateGame(game);
        }

        Sesh sess = null;
        var sessions = webSocketService.getSessions(game.gameID());
        for (var sesh : sessions) {
            if(sesh.session == session) {
                sess = sesh;
            }
        }
        webSocketService.remove(game.gameID(), sess);
        var message = new NotifyMessage(user + " has left the game");
        broadcast(game.gameID(), message, session);
    }

    private void resign(ResignCommand resignCommand) throws Exception {
        String user = loginService.getUsername(resignCommand.getAuthToken());
        var game = gameService.getGame(resignCommand.getGameID());

        if (!game.whiteUsername().equals(user) && !game.blackUsername().equals(user)) {
            throw new Exception("You are not allowed to resign this game as Observer");
        }
        if (game.game().getTeamTurn() == ChessGame.TeamColor.NONE) {
            throw new Exception("Game is Over");
        }

        game.game().setTeamTurn(ChessGame.TeamColor.NONE);
        gameService.updateGame(game);
        var message = new NotifyMessage(user + " has resigned");
        broadcast(game.gameID(), message, null);
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
            message = new NotifyMessage(user + " has joined as an Observer");
            broadcast(id, message, session);
        }
        else if (team == ChessGame.TeamColor.WHITE) {
            message = new NotifyMessage(user + " has joined as " + team.toString());
            broadcast(id, message, session);
        }
        else if (team == ChessGame.TeamColor.BLACK) {
            message = new NotifyMessage(user + " has joined as " + team.toString());
            broadcast(id, message, session);
        }
        message = new LoadMessage(gameData);
        sendMessage(message, session);
    }

    private void move(MoveCommand moveCommand, Session session) throws Exception {
        String user = loginService.getUsername(moveCommand.getAuthToken());
        var game = gameService.getGame(moveCommand.getGameID());
        ChessGame.TeamColor team = game.game().getTeamTurn();

        String turn = switch(team) {
            case WHITE -> game.whiteUsername();
            case BLACK -> game.blackUsername();
            case NONE -> throw new Exception("the game is over");
        };

        if(!Objects.equals(user, turn)) {
            throw new Exception("not your turn");
        }
        game.game().makeMove(moveCommand.getMove());
        gameService.updateGame(game);

        broadcast(game.gameID(), new LoadMessage(game), null);
        var message = new NotifyMessage(user + " has moved " + moveCommand.getMove().toString());
        broadcast(game.gameID(), message, session);

        if(game.game().isInCheckmate(game.game().getTeamTurn())) {
            broadcast(game.gameID(), new NotifyMessage("checkmate"), null);
            game.game().setTeamTurn(ChessGame.TeamColor.NONE);
        }
        else if(game.game().isInCheck(game.game().getTeamTurn())) {
            broadcast(game.gameID(), new NotifyMessage("check"), null);
        }
        else if (game.game().isInStalemate(game.game().getTeamTurn())) {
            broadcast(game.gameID(), new NotifyMessage("stalemate"), null);
            game.game().setTeamTurn(ChessGame.TeamColor.NONE);
        }
    }

    private void sendMessage(ServerMessage message, Session session) throws Exception {
        if(session.isOpen()) {
            session.getRemote().sendString(new Gson().toJson(message));
        }
    }

    private void broadcast(int gameID, ServerMessage message, Session exempt) throws Exception {
        var sessions = webSocketService.getSessions(gameID);
        for (var session : sessions) {
            if(session.session != exempt && session.session.isOpen()) {
                if(message.getClass() == LoadMessage.class) {
                    if (Objects.equals(session.name, gameService.getGame(gameID).blackUsername())) {
                        ((LoadMessage) message).setTeamColor(ChessGame.TeamColor.BLACK);
                    }
                    else {
                        ((LoadMessage) message).setTeamColor(ChessGame.TeamColor.WHITE);
                    }
                }
                sendMessage(message, session.session);
            }
        }
    }
}
