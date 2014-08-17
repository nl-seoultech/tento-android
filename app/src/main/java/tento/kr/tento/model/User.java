package tento.kr.tento.model;

import android.os.AsyncTask;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class User {
    private int id;
    private String email;
    private String name;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public User(int id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
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

    public interface TentoCallback {
        public void success(String result);
        public void error();
    }

    public void create(String password, final TentoCallback callback) {
        String url = "http://10.0.3.2:5000/users/";
        RequestBody formBody = new FormEncodingBuilder()
                .add("email", this.email)
                .add("password", password)
                .build();
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        new AsyncTask<Request, Void, String>() {

            @Override
            protected String doInBackground(Request... requests) {
                String result = null;
                Request request = requests[0];
                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if(result == null) {
                    callback.error();
                } else {
                    callback.success(result);
                }
            }
        }.execute(request);
    }
}
