package ui;

import chess.ChessGame;
import model.*;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import static ui.EscapeSequences.*;

public class Client {
    public static UserState uState;
    private ServerFacade sv;
    private LoopR loop;
    private ArrayList<GameResult> games;
    private ConcurrentHashMap<Integer, GameData> gameObj;
    private GameData game;
    private ChessGame.TeamColor team;
    private AuthData auth;

    public Client(String url, LoopR loop) {
        uState = UserState.LOGGED_OUT;
        this.loop = loop;
        sv = new ServerFacade(url);
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
                case "observe" -> observe (params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
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

        return "bad request: double check parameters";
    }

    private String login(String[] params) {
        if (uState == UserState.LOGGED_IN) {
            return "Logout before logining a new user";
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
        return "bad request: double check parameters";
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
            gameID = sv.create(name);
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
            int ID = Integer.parseInt(params[0]);
            game = gameObj.get(ID);
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

            sv.join(new JoinRequest(tempteam, ID, auth.authToken()));

            if(tempteam == ChessGame.TeamColor.BLACK) {
                game = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
            } else if (tempteam == ChessGame.TeamColor.WHITE) {
                game = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
            }

            gameObj.put(ID, game);
            uState = UserState.IN_GAME;
            team = tempteam;
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
        sb.append(String.format("| ID | %-14s| %14s| %-12s|\n", "White", "Black", "Name"));
        sb.append("\n");

        for (GameResult each : games) {
            sb.append(String.format("| %-4d| %-14s| %-14s| %-12s|\n", each.gameID(), each.whiteUsername(), each.blackUsername(), each.gameName()));
        }

        return String.valueOf(sb);
    }

    private String observe(String[] params) {
        if (uState == UserState.LOGGED_OUT) {
            return "Must be logged in to observe";
        }
        try {
            games = sv.list();
            int ID = Integer.parseInt(params[0]);
            game = gameObj.get(ID);

            sv.join(new JoinRequest(null, ID, auth.authToken()));
            uState = UserState.IN_GAME;
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
        else {
            cmds = new String[] {"create <NAME>", "list", "join <ID> [WHITE|BLACK|<empty>]", "observe <ID>", "logout", "quit", "help"};
            descrip = new String[]{"create a game with provided name",
                    "list all games",
                    "joins the provided game to play as the provided team",
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
}
