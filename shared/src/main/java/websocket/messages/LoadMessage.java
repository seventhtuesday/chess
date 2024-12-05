package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadMessage extends ServerMessage {
    private final GameData gameData;
    private ChessGame.TeamColor teamColor;

    public LoadMessage(GameData game) {
        super(ServerMessageType.LOAD_GAME);
        this.gameData = game;
    }

    public GameData getGameData() {
        return gameData;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }
}
