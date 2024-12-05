package websocket.commands;

import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
    private final ChessGame.TeamColor teamColor;

    public ConnectCommand(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        super(CommandType.CONNECT, authToken, gameID);
        this.teamColor = teamColor;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
