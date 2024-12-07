package ui;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotifyMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class LoopR {
    private final Client cli;

    public LoopR(String url) {
        cli = new Client(url, this);
    }

    public void run() {
        System.out.print(SET_BG_COLOR_WHITE);
        System.out.println("👑 Welcome to 240 Chess. Type Help to get started. 👑");

        Scanner sc = new Scanner(System.in);
        var out = "";

        while (!out.equals("quit")) {
            prompt();

            String in = sc.nextLine();

            try {
                out = cli.out(in);
                System.out.println(SET_TEXT_COLOR_BLUE + out);
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println();
    }

    public void notify(NotifyMessage message) {
        System.out.print(SET_TEXT_COLOR_YELLOW + message.getMessage());
        prompt();
    }

    public void load(LoadMessage message) {
        var game = message.getGameData();
        cli.updateGame(game);
        var team = message.getTeamColor();
        PrintBoard.run(game.game(), team);
        prompt();
    }

    public void error(ErrorMessage message) {
        System.out.print(SET_TEXT_COLOR_RED + message.getErrorMess());
        prompt();
    }

    private void prompt() {
        System.out.print(SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + "[" + Client.uState + "] >>> " + SET_TEXT_COLOR_GREEN);
    }
}
