package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String errorMess;
    public ErrorMessage(String errorMess) {
        super(ServerMessageType.ERROR);
        this.errorMess = errorMess;
    }

    public String getErrorMess() {
        return errorMess;
    }
}
