package tento.kr.tento.model;

import android.os.AsyncTask;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
