package kr.tento.model;

import com.squareup.okhttp.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import kr.tento.api.TentoResponse;
import kr.tento.api.UserAPI;

public class User {
    private int id;
    private String email;
    private String name;
    private String password;
    private Token token;
    private UserAPI userApi = new UserAPI();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public User(int id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public User(JSONObject o) throws JSONException {
        id = o.getInt("id");
        name = o.getString("name");
        email = o.getString("email");
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void create() {
        userApi.create(this, new TentoResponse.single<User>() {
            @Override
            public void ok(User o) {
            }

            @Override
            public void fail(String f) {
            }
        });
    }

    public void login() {

        userApi.login(this, new TentoResponse.single<User>() {
            @Override
            public void ok(User o) {
                setId(o.getId());
                setToken(o.getToken());
            }

            @Override
            public void fail(String f) {
            }
        });
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
