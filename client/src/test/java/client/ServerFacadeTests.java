package client;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade sf;
    static Integer ID;
    static AuthData auth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        sf = new ServerFacade(url);
    }

    @BeforeEach
    public void startup() {
        try {
            sf.clear();
            auth = sf.register(new UserData("test", "test", "test@email.com"));
            ID = sf.create(new CreateRequest("test", auth.authToken()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void shutdown() {
        try {
            sf.clear();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerGood() throws Exception {
        AuthData authD =sf.register(new UserData("test2", "test2", "test2@email.com"));
        Assertions.assertTrue(authD != null);
    }

    @Test
    public void registerBad() throws Exception {
        sf.register(new UserData("test2", "test2", "test2@email.com"));
        Assertions.assertThrows(Exception.class, () -> sf.register(new UserData("test2", "test2", "test2@email.com")));
    }

    @Test
    public void loginGood() {
        Assertions.assertDoesNotThrow(() -> sf.login(new UserData("test", "test", null)));
    }

    @Test
    public void loginBad() {
        Assertions.assertThrows(Exception.class, () -> sf.login(new UserData("test2", "test2", null)));
    }

    @Test
    public void logoutGood() throws Exception {
        Assertions.assertDoesNotThrow(() -> sf.logout());
    }

    @Test
    public void logoutBad() throws Exception {
        sf.logout();
        Assertions.assertThrows(Exception.class, () -> sf.logout());
    }

    @Test
    public void createGood() {
        Assertions.assertDoesNotThrow(() -> sf.create(new CreateRequest("test2", auth.authToken())));
    }

    @Test
    public void createBad() throws Exception {
        sf.logout();
        Assertions.assertThrows(Exception.class, () -> sf.create(new CreateRequest("test2", auth.authToken())));
    }

    @Test
    public void listGood() throws Exception {
        sf.create(new CreateRequest("test2", auth.authToken()));
        Assertions.assertDoesNotThrow(() -> sf.list());
    }

    @Test
    public void listBad() throws Exception {
        sf.logout();
        Assertions.assertThrows(Exception.class, () -> sf.list());
    }

    @Test
    public void joinGood() {
        try {
            sf.join(new JoinRequest(ChessGame.TeamColor.BLACK, ID, auth.authToken()));
            Collection<GameResult> games = sf.list();
            Collection<GameResult> real = new ArrayList<>();
            real.add(new GameResult(ID, null, "test", "test"));
            Assertions.assertEquals(real, games);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void joinBad() {
        Assertions.assertThrows(Exception.class, () -> sf.join(new JoinRequest(null, 0, null)));
    }
}
