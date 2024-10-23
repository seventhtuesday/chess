package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import service.*;
import spark.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Server {
    private RegService regS;
    private LoginService loginS;
    private LogoutService logoutS;
    private GameService gameS;
    private JoinService joinS;
    private ListService listS;
    private ClearService clearS;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        regS = new RegService(userDAO, authDAO);
        loginS = new LoginService(userDAO, authDAO);
        logoutS = new LogoutService(authDAO);
        gameS = new GameService(authDAO, gameDAO);
        joinS = new JoinService(authDAO, gameDAO);
        listS = new ListService(authDAO, gameDAO);
        clearS = new ClearService(userDAO, authDAO, gameDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::regHand);
        Spark.post("/session", this::loginHand);
        Spark.delete("/session", this::logoutHand);
        Spark.post("/game", this::gameHand);
        Spark.put("/game", this::joinHand);
        Spark.get("/game", this::listHand);
        Spark.delete("/db", this::clearHand);

        Spark.exception(DataAccessException.class, this::dataExceptHand);
        Spark.exception(Exception.class, this::exceptHand);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private String dataExceptHand(DataAccessException e, Request req, Response res) {
        res.type("application/json");
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));

        res.status(500);
        res.body(body);
        return body;
    }

    private String exceptHand(Exception e, Request req, Response res) {
        res.type("application/json");
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));

        if(Objects.equals(e.getMessage(), "bad request")) {
            res.status(400);
            res.body(body);
        }
        else if (Objects.equals(e.getMessage(), "unauthorized")) {
            res.status(401);
            res.body(body);
        }
        else if (Objects.equals(e.getMessage(), "already taken")) {
            res.status(403);
            res.body(body);
        }
        else {
            res.status(500);
            res.body(body);
        }
        return body;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object regHand(Request req, Response res) throws Exception {
        res.type("application/json");
        var newUser = new Gson().fromJson(req.body(), UserData.class);

        AuthData authData = regS.registerUser(newUser);

        res.status(200);
        res.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

    private Object loginHand(Request req, Response res) throws Exception {
        res.type("application/json");
        var login = new Gson().fromJson(req.body(), LoginRequest.class);

        AuthData authData = loginS.login(login);

        res.status(200);
        res.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

    private Object logoutHand(Request req, Response res) throws Exception {
        res.type("application/json");
        var logout = new AuthRequest(req.headers("Authorization"));

        logoutS.logout(logout);

        res.status(200);
        return "{}";
    }

    private Object gameHand(Request req, Response res) throws Exception {
        res.type("application/json");
        var auth = req.headers("Authorization");
        var name = new Gson().fromJson(req.body(), CreateRequest.class);
        var game = new CreateRequest(name.gameName(), auth);

        CreateResult gameID = gameS.createGame(game);

        res.status(200);
        res.body(new Gson().toJson(gameID));
        return new Gson().toJson(gameID);
    }

    private Object joinHand(Request req, Response res) throws Exception {
        res.type("application/json");
        var auth = req.headers("Authorization");
        var name = new Gson().fromJson(req.body(), JoinRequest.class);
        var game = new JoinRequest(name.playerColor(), name.gameID(), auth);

        joinS.join(game);

        res.status(200);
        return "{}";
    }

    private Object listHand(Request req, Response res) throws Exception {
        res.type("application/json");
        var list = new AuthRequest(req.headers("Authorization"));
        ArrayList<GameResult> games = listS.list(list);

        var body = new Gson().toJson(new ListResponse(games));

        res.status(200);
        res.body(body);
        return body;
    }

    private Object clearHand(Request req, Response res) throws Exception {
        res.type("application/json");

        clearS.clear();

        res.status(200);
        return "{}";
    }
}
