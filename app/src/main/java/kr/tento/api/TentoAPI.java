package kr.tento.api;

import android.os.AsyncTask;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.tento.model.User;

public class TentoAPI<T> {

    String host = "192.168.0.10";
    int port = 5000;

    protected T parseSingle(Class<T> c, JSONObject o) {
        T r = null;
        try {
            if(c.equals(User.class)) {
                r = (T) new User(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return r;
    }

    protected T parseOne(Class<T> c, String jsonable) {
        return parseOne(c, jsonable, c.getName().toLowerCase());
    }

    protected T parseOne(Class<T> c, String jsonable, String key) {
        T result = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonable);
            result = parseSingle(c, jsonObject.getJSONObject(key));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected List<T> parseCollection(Class<T> c, String jsonable) {
        return parseCollection(c, jsonable, c.getName().toLowerCase() + "s");
    }

    protected List<T> parseCollection(Class<T> c, String jsonable, String key) {
        try {
            JSONObject jsonObject = new JSONObject(jsonable);
            JSONArray a = jsonObject.getJSONArray(key);
            List<T> ms = new ArrayList<T>();
            for(int i = 0; i < a.length(); i++) {
                ms.add(parseSingle(c, a.getJSONObject(i)));
            }
            return ms;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private URI buildURI(String path)  {
        try {
            return new URI("http", null, host, port, path, null, null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void form(
            String path, HashMap<String, String> formData, TentoResponse.Callback callback) {
        formWithQuery(path, null, formData, callback);
    }

    protected void formWithQuery(
            String path, HashMap<String, String> query,
            HashMap<String, String> formData, TentoResponse.Callback callback) {

        FormEncodingBuilder formBody = new FormEncodingBuilder();
        Iterator<Map.Entry<String, String>> entries = formData.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, String> entry = entries.next();
            formBody.add(entry.getKey(), entry.getValue());
        }

        if(query != null) {
        }

        makeFormPost(buildURI(path).toString(), formBody.build(), callback);
    }

    private void makeFormPost(String url, RequestBody body, final TentoResponse.Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        final OkHttpClient client = new OkHttpClient();
        new AsyncTask<Request, Void, String>() {
            @Override
            protected String doInBackground(Request... requests) {
                String result = null;
                Request request = requests[0];
                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if(result == null) {
                    callback.fail(result);
                } else {
                    callback.ok(result);
                }
            }
        }.execute(request);
    }
}
