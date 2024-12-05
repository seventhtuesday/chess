package websocket;


import org.eclipse.jetty.websocket.api.Session;

public class Sesh {
    public String name;
    public Session session;

    public Sesh(String name, Session session) {
        this.name = name;
        this.session = session;
    }
}
