package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class PrintBoard {

    public static void run(ChessGame game, ChessGame.TeamColor team) {
        var ps = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ps.println(ERASE_SCREEN);

        if(team == ChessGame.TeamColor.BLACK) {
            blackHead(ps);
            blackBoard(ps, game);
            blackHead(ps);
        }
        else {
            whiteHead(ps);
            whiteBoard(ps, game);
            whiteHead(ps);
        }
    }

    private static void blackHead(PrintStream ps) {
        ps.print(SET_BG_COLOR_DARK_GREY);
        ps.print(SET_TEXT_COLOR_BLACK);
        ps.println("    h  g  f  e  d  c  b  a    ");
    }

    private static void whiteHead(PrintStream ps) {
        ps.print(SET_BG_COLOR_DARK_GREY);
        ps.print(SET_TEXT_COLOR_BLACK);
        ps.println("    a  b  c  d  e  f  g  h    ");
    }

    private static void blackBoard(PrintStream ps, ChessGame game) {
        ChessBoard board = game.getBoard();

        for (int i = 1; i <= 8; i++) {
            ps.print(SET_BG_COLOR_DARK_GREY);
            ps.print(SET_TEXT_COLOR_BLACK);
            ps.print(" " + i + " ");

            for (int j = 8; j >= 1; j--) {
                if ((i+j) % 2 == 1) {
                    ps.print(SET_BG_COLOR_LIGHT_GREY);
                    printPiece(ps, board.getPiece(new ChessPosition(i, j)));
                }
                else {
                    ps.print(SET_BG_COLOR_BLACK);
                    printPiece(ps, board.getPiece(new ChessPosition(i, j)));
                }
            }

            ps.print(SET_BG_COLOR_DARK_GREY);
            ps.print(SET_TEXT_COLOR_BLACK);
            ps.print(" " + i + " ");
            ps.print("\n");
        }
    }

    private static void whiteBoard(PrintStream ps, ChessGame game) {
        ChessBoard board = game.getBoard();

        for (int i = 8; i >= 1; i--) {
            ps.print(SET_BG_COLOR_DARK_GREY);
            ps.print(SET_TEXT_COLOR_BLACK);
            ps.print(" " + i + " ");

            for (int j = 1; j <= 8; j++) {
                if ((i+j) % 2 == 0) {
                    ps.print(SET_BG_COLOR_BLACK);
                    printPiece(ps, board.getPiece(new ChessPosition(i, j)));
                }
                else {
                    ps.print(SET_BG_COLOR_LIGHT_GREY);
                    printPiece(ps, board.getPiece(new ChessPosition(i, j)));
                }
            }

            ps.print(SET_BG_COLOR_DARK_GREY);
            ps.print(SET_TEXT_COLOR_BLACK);
            ps.print(" " + i + " ");
            ps.print("\n");
        }
    }

    private static void printPiece(PrintStream ps, ChessPiece piece) {
        if(piece == null) {
            ps.print("   ");
            return;
        }
        else if(piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            ps.print(SET_TEXT_COLOR_BLUE);
        }
        else {
            ps.print(SET_TEXT_COLOR_RED);
        }

        switch (piece.getPieceType()) {
            case PAWN:
                ps.print(" P ");
                break;
            case KNIGHT:
                ps.print(" N ");
                break;
            case BISHOP:
                ps.print(" B ");
                break;
            case ROOK:
                ps.print(" R ");
                break;
            case QUEEN:
                ps.print(" Q ");
                break;
            case KING:
                ps.print(" K ");
                break;
        }
    }

    public static void highlight(ChessGame game, ChessGame.TeamColor team, ChessPosition start) {
        var ps = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ps.println(ERASE_SCREEN);

        if(team == ChessGame.TeamColor.BLACK) {
            blackHead(ps);
            blackBoardHigh(ps, game, start);
            blackHead(ps);
        }
        else if(team == ChessGame.TeamColor.WHITE) {
            whiteHead(ps);
            whiteBoardHigh(ps, game, start);
            whiteHead(ps);
        }
    }

    private static void blackBoardHigh(PrintStream ps, ChessGame game, ChessPosition start) {
        ChessBoard board = game.getBoard();
        var moves = game.validMoves(start);

        for (int i = 1; i <= 8; i++) {
            ps.print(SET_BG_COLOR_DARK_GREY);
            ps.print(SET_TEXT_COLOR_BLACK);
            ps.print(" " + i + " ");

            for (int j = 8; j >= 1; j--) {
                var end = new ChessPosition(i, j);

                if (moves.contains(new ChessMove(start, end))) {
                    ps.print(SET_BG_COLOR_GREEN);
                } else if ((i + j) % 2 == 1) {
                    ps.print(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    ps.print(SET_BG_COLOR_BLACK);
                }
                printPiece(ps, board.getPiece(new ChessPosition(i, j)));
            }

            ps.print(SET_BG_COLOR_DARK_GREY);
            ps.print(SET_TEXT_COLOR_BLACK);
            ps.print(" " + i + " ");
            ps.print("\n");
        }
    }

    private static void whiteBoardHigh(PrintStream ps, ChessGame game, ChessPosition start) {
        ChessBoard board = game.getBoard();
        var moves = game.validMoves(start);

        for (int i = 8; i >= 1; i--) {
            ps.print(SET_BG_COLOR_DARK_GREY);
            ps.print(SET_TEXT_COLOR_BLACK);
            ps.print(" " + i + " ");

            for (int j = 1; j <= 8; j++) {
                var end = new ChessPosition(i, j);

                if (moves.contains(new ChessMove(start, end))) {
                    ps.print(SET_BG_COLOR_GREEN);
                } else if ((i + j) % 2 == 0) {
                    ps.print(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    ps.print(SET_BG_COLOR_BLACK);
                }
                printPiece(ps, board.getPiece(new ChessPosition(i, j)));
            }

            ps.print(SET_BG_COLOR_DARK_GREY);
            ps.print(SET_TEXT_COLOR_BLACK);
            ps.print(" " + i + " ");
            ps.print("\n");
        }
    }
}
