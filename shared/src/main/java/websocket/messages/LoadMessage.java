package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadMessage extends ServerMessage {
    private final GameData game;
    private ChessGame.TeamColor teamColor;

    public LoadMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public GameData getGameData() {
        return game;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }
}
