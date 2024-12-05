package ui;

import chess.*;
import model.*;
import server.ServerFacade;
import websocket.SocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static ui.EscapeSequences.*;

public class Client {
    public static UserState uState;
    private final ServerFacade sv;
    private final LoopR loop;
    private final SocketFacade ws;
    private ArrayList<GameResult> games;
    private final ConcurrentHashMap<Integer, GameData> gameObj = new ConcurrentHashMap<>();
    private GameData game;
    private ChessGame.TeamColor team;
    private AuthData auth;

    public Client(String url, LoopR loop) {
        uState = UserState.LOGGED_OUT;
        sv = new ServerFacade(url);
        this.loop = loop;
        ws = new SocketFacade(url, loop);
    }

    public String out(String in) {
        try {
            var args = in.split(" ");
            var command = (args.length > 0) ? args[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(args, 1, args.length);

            return switch (command) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout(params);
                case "create" -> create(params);
                case "join" -> join(params);
                case "list" -> list(params);
                case "observe" -> observe(params);
                case "quit" -> "quit";
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "resign" -> resign();
                case "move" -> move(params);
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String highlight(String[] params) throws Exception {
        if (uState != UserState.IN_GAME) {
            return "You are not in a game";
        }
        if(params.length != 1) {
            return "only enter one start position";
        }

        PrintBoard.highlight(game.game(), team, interpretMove(params[0]));
        return "";
    }

    private String move(String[] params) throws Exception {
        if (uState != UserState.IN_GAME) {
            return "You are not in a game";
        }
        if (params.length < 2) {
            return "Invalid Move";
        }

        var start = params[0];
        var end = params[1];
        ChessPosition startPos = interpretMove(start.toUpperCase());
        ChessPosition endPos = interpretMove(end.toUpperCase());

        ChessPiece.PieceType promotion = null;
        if (params[2] != null) {
            promotion = ChessPiece.PieceType.valueOf(params[2].toUpperCase());
        }
        ChessMove move = new ChessMove(startPos, endPos, promotion);
        ws.move(auth.authToken(), game.gameID(), move);
        return "";
    }

    private ChessPosition interpretMove(String location) throws Exception {
        if (location.length() != 2) {
            throw new Exception("Invalid position" + location);
        }

        var col = (int) location.charAt(0) - 64;
        int row = Integer.parseInt(location.substring(1));
        return new ChessPosition(row, col);

    }

    private String resign() {
        if (uState != UserState.IN_GAME) {
            return "You are not in a game";
        }

        ws.resign(auth.authToken(), game.gameID());
        uState = UserState.LOGGED_IN;
        return "";
    }

    private String leave() {
        if (uState != UserState.IN_GAME) {
            return "You are not in a game";
        }

        ws.leave(auth.authToken(), game.gameID());
        uState = UserState.LOGGED_IN;
        return "";
    }

    private String redraw() {
        if (uState != UserState.IN_GAME) {
            return "You are not in a game";
        }

        PrintBoard.run(game.game(), team);
        return "";
    }

    private String register(String[] params) {
        if (uState != UserState.LOGGED_OUT) {
            return "Logout before registering a new user";
        }

        if (params.length == 3) {
            UserData user = new UserData(params[0], params[1], params[2]);
            try {
                auth = sv.register(user);
            } catch (Exception e) {
                return "bad request: one of your parameters could not be accepted";
            }
            uState = UserState.LOGGED_IN;
            return "Logged in as " + auth.username();
        }

        return "bad request: invalid credentials";
    }

    private String login(String[] params) {
        if (uState == UserState.LOGGED_IN) {
            return "Logout before logging in a new user";
        }
        if (params.length == 2) {
            UserData user = new UserData(params[0], params[1], null);
            try {
                auth = sv.login(user);
            } catch (Exception e) {
                return "invalid login";
            }
            uState = UserState.LOGGED_IN;
            return "Logged in as " + auth.username();
        }
        return "bad request: invalid credentials";
    }

    private String logout(String[] params) {
        if (uState == UserState.LOGGED_OUT) {
            return "you must be logged in to logout";
        }
        uState = UserState.LOGGED_OUT;
        try {
            sv.logout();
        } catch (Exception e) {
            return "unable to log out";
        }
        return "Logged out as " + auth.username();
    }

    private String create(String[] params) {
        if (uState == UserState.LOGGED_OUT) {
            return "Must be logged in to create a game";
        }
        String name = String.join(" ", params);
        Integer gameID;
        try {
            gameID = sv.create(new CreateRequest(name, auth.authToken()));
            games = sv.list();
            game = new GameData(gameID, null, null, name, new ChessGame());
            gameObj.put(gameID, game);
            return "Created game " + gameID + ": " + name;
        } catch (Exception e) {
            return "unable to create game";
        }
    }

    private String join(String[] params) {
        if (uState == UserState.LOGGED_OUT) {
            return "Must be logged in to join a game";
        }

        try {
            games = sv.list();
            int index = Integer.parseInt(params[0]) - 1;
            int id = games.get(index).gameID();
            game = gameObj.get(id);
            var color = params[1].toUpperCase();
            ChessGame.TeamColor tempteam = null;

            if (color.equals("WHITE") || color.equals("BLACK")) {
                tempteam = ChessGame.TeamColor.valueOf(color);
            }
            else if (game.whiteUsername() != null) {
                return "team already occupied";
            }
            else {
                return "invalid team";
            }

            sv.join(new JoinRequest(tempteam, id, auth.authToken()));

            if(tempteam == ChessGame.TeamColor.BLACK) {
                GameData tempGame = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
                game = tempGame;
            } else if (tempteam == ChessGame.TeamColor.WHITE) {
                var tempGame = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
                game = tempGame;
            }

            gameObj.put(id, game);
            uState = UserState.IN_GAME;
            team = tempteam;

            PrintBoard.run(game.game(), ChessGame.TeamColor.BLACK);
            PrintBoard.run(game.game(), ChessGame.TeamColor.WHITE);
            return "";

        } catch (Exception e) {
            return "unable to join game";
        }
    }

    private String list(String[] params) {
        if (uState == UserState.LOGGED_OUT) {
            return "Must be logged in to list games";
        }
        try {
            games = sv.list();
        } catch (Exception e) {
            return "error: list update failed";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(String.format("| ID | %-14s | %14s | %-12s |\n", "White", "Black", "Name"));
        sb.append("\n");

        for (int n = 0; n < games.size(); n++) {
            sb.append(String.format("| %-4d | %-14s | %-14s | %-12s |\n",
                    n+1, games.get(n).whiteUsername(), games.get(n).blackUsername(), games.get(n).gameName()));
        }

        return String.valueOf(sb);
    }

    private String observe(String[] params) {
        if (uState == UserState.LOGGED_OUT) {
            return "Must be logged in to observe";
        }
        try {
            games = sv.list();
            int index = Integer.parseInt(params[0]) - 1;
            int id = games.get(index).gameID();
            game = gameObj.get(id);

            uState = UserState.IN_GAME;

            PrintBoard.run(game.game(), ChessGame.TeamColor.BLACK);
            PrintBoard.run(game.game(), ChessGame.TeamColor.WHITE);
            return "";

        } catch (Exception e) {
            return "unable to observe";
        }
    }

    private String help() {
        String[] cmds = {};
        String[] descrip = {};

        if(uState == UserState.LOGGED_OUT) {
            cmds = new String[] {"register <USERNAME> <PASSWORD> <EMAIL>", "login <USERNAME> <PASSWORD>", "quit", "help"};
            descrip = new String[] {"create an account", "login to existing account", "quit the app", "list available commands"};
        }
        else if(uState == UserState.IN_GAME) {
            cmds = new String[]{"redraw", "move <START> <END> [<PIECE>|<empty>]", "highlight <START>", "resign", "leave", "help"};
            descrip = new String[]{"redraw the chess board",
                    "move a piece from <START> to <END>; use <PIECE> if promoting a pawn to designate promotion",
                    "highlight the legal moves for the piece at <START>",
                    "resign the game",
                    "leave the game",
                    "list available commands"};
        }
        else {
            cmds = new String[] {"create <NAME>", "list", "join <ID> [WHITE|BLACK|<empty>]", "observe <ID>", "logout", "quit", "help"};
            descrip = new String[]{"create a game with provided name",
                    "list all games",
                    "join the provided game to play as the provided team",
                    "watch the provided game",
                    "logs out the current user",
                    "quit the app",
                    "list available commands"};
        }

        StringBuilder ans = new StringBuilder();
        for (int n = 0; n < cmds.length; n++) {
            ans.append(SET_TEXT_COLOR_BLUE);
            ans.append(" - ");
            ans.append(cmds[n]);
            ans.append(SET_TEXT_COLOR_MAGENTA);
            ans.append(" - ");
            ans.append(descrip[n]);
            ans.append("\n");
        }
        return ans.toString();
    }

    public UserState getUserState() {
        return uState;
    }
}
