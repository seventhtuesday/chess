package server;

import com.google.gson.Gson;
import dataAccess.*;
import model.*;
import service.*;
import spark.*;

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

        RegService regS = new RegService(userDAO, authDAO);
        LoginService loginS = new LoginService(userDAO, authDAO);
        LogoutService logoutS = new LogoutService(authDAO);
        GameService gameS = new GameService(authDAO, gameDAO);
        JoinService joinS = new JoinService(authDAO, gameDAO);
        ListService listS = new ListService(authDAO, gameDAO);
        ClearService clearS = new ClearService(userDAO, authDAO, gameDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", regHand);
        Spark.post("/session", loginHand);
        Spark.delete("/session", logoutHand);
        Spark.post("/game", gameHand);
        Spark.put("/game", joinHand);
        Spark.get("/game", listHand);
        Spark.delete("/db", clearHand);

        Spark.exception(DataAccessException.class, dataExceptHand);
        Spark.exception(Exception.class, exceptHand);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void dataExceptHand(DataAccessException e, Request req, Response res) {
        res.status(500);
        res.body(new Gson().toJson(e.getMessage()));
    }

    private void exceptHand(Exception e, Request req, Response res) {
        if(Objects.equals(e.getMessage(), "error: bad request")) {
            res.status(400);
            res.body(new Gson().toJson(e.getMessage()));
        }
        else if (Objects.equals(e.getMessage(), "error: unauthorized")) {
            res.status(401);
            res.body(new Gson().toJson(e.getMessage()));
        }
        else if (Objects.equals(e.getMessage(), "error: already taken")) {
            res.status(403);
            res.body(new Gson().toJson(e.getMessage()));
        }
        else {
            res.status(500);
            res.body(new Gson().toJson(e.getMessage()));
        }
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
}
