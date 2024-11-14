package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class PrintBoard {

    public static void main(ChessGame game, ChessGame.TeamColor team) {
        var ps = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        var board = game.getBoard();

        ps.println(ERASE_SCREEN);

        if(team == ChessGame.TeamColor.BLACK) {
            blackHead(ps);
            blackBoard(ps, game);
            blackHead(ps);
        }
        else if(team == ChessGame.TeamColor.WHITE) {
            whiteHead(ps);
            whiteBoard(ps, game);
            whiteHead(ps);
        }
    }

    private static void blackHead(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.println("    h  g  f  e  d  c  b  a    ");
    }

    private static void whiteHead(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.println("    a  b  c  d  e  f  g  h    ");
    }

    private static void blackBoard(PrintStream out, ChessGame game) {
        ChessBoard board = game.getBoard();

        for (int i = 1; i <= 8; i++) {
            out.print(SET_BG_COLOR_DARK_GREY);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(" " + i + " ");

            for (int j = 1; j <= 8; j++) {
                if (i % 2 == 1) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    if(board.getPiece(new ChessPosition(i, j)) == null);
                }
                else {
                    out.print(SET_BG_COLOR_BLACK);

                }
            }

            out.print(SET_BG_COLOR_DARK_GREY);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(" " + i + " ");
        }
    }

    private static void whiteBoard(PrintStream out, ChessGame game) {

    }
}
