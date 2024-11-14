package server;

import com.google.gson.Gson;
import model.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ServerFacade {
    private final String url;
    private String token;

    public ServerFacade(String url) {
        this.url = url;
    }

    public AuthData register(UserData user) throws Exception {
        String path = "/user";
        AuthData authData = this.request("POST", path, user, AuthData.class);
        if (authData != null) {
            token = authData.authToken();
        }
        return authData;
    }

    public AuthData login(UserData user) throws Exception {
        String path = "/session";
        AuthData authData = this.request("POST", path, user, AuthData.class);
        if (authData != null) {
            token = authData.authToken();
        }
        return authData;
    }

    public void logout() throws Exception {
        String path = "/session";
        this.request("DELETE", path, null, null);
        token = null;
    }

    public Integer create(CreateRequest cre) throws Exception {
        String path = "/game";
        var res = this.request("POST", path, cre, CreateResult.class);
        return res.gameID();
    }

    public void join(JoinRequest join) throws Exception {
        String path = "/game";
        this.request("POST", path, join, null);
    }

    public ArrayList<GameResult> list() throws Exception {
        String path = "/game";
        return this.request("GET", path, null, ListResult.class).games();
    }

    private <T> T request(String method, String path, Object request, Class<T> response) throws Exception {
        try {
            URL url = (new URI(this.url + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if(token != null) {
                http.setRequestProperty("Authorization", token);
            }

            write(request, http);
            http.connect();
            var status = http.getResponseCode();
            if (status != 200) {
                throw new Exception("failure: " + status);
            }
            return read(http, response);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void write(Object request, HttpURLConnection http) throws Exception {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String requestString = new Gson().toJson(request);
            try {
                OutputStream os = http.getOutputStream();
                os.write(requestString.getBytes());
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    private <T> T read(HttpURLConnection http, Class<T> response) throws Exception {
        T res = null;
        if (http.getContentLength() < 0) {
            try {
                InputStream stream = http.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream);
                if(response != null) {
                    res = new Gson().fromJson(reader, response);
                }
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        return res;
    }
}
