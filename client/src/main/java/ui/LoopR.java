package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class LoopR {
    private Client cli;

    public LoopR(String url) {
        cli = new Client(url, this);
    }

    public void run() {
        System.out.print(SET_BG_COLOR_WHITE);
        System.out.println("👑 Welcome to 240 Chess. Type Help to get started. 👑");

        Scanner sc = new Scanner(System.in);
        var out = "";

        while (!out.equals("quit")) {
            System.out.print(SET_TEXT_COLOR_BLACK + "/n" + "[" + Client.uState + "] >>> " + SET_TEXT_COLOR_GREEN);

            String in = sc.nextLine();

            try {
                out = cli.out(in);
                System.out.println(SET_TEXT_COLOR_BLUE + out);
                if ()
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println();
    }
}
