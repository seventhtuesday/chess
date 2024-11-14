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
    private String url;
    private LoopR loop;
    private ArrayList<GameResult> games;
    private ConcurrentHashMap<Integer, GameData> gameObj;
    private GameData game;
    private ChessGame.TeamColor team;
    private AuthData auth;

    public Client(String url, LoopR loop) {
        uState = UserState.LOGGED_OUT;
        this.url = url;
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
