package kr.tento.model;

import com.squareup.okhttp.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String email;
    private String name;
    private String password;

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

    public interface TentoCallback {
        public void success(String result);
        public void error();
    }
}
