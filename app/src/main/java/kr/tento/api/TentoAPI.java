package kr.tento.api;

import android.os.AsyncTask;
import android.util.Pair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
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

import kr.tento.model.Token;
import kr.tento.model.User;

public class TentoAPI<T> {

    String host = "192.168.0.10";
    int port = 5000;

    protected T parseSingle(Class<T> c, JSONObject o) {
        T r = null;
        try {
            if(c.equals(User.class)) {
                r = (T) new User(o);
            } else if(c.equals(Token.class)) {
                r = (T) new Token(o);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return r;
    }

    protected T parseSingle(Class<T> c, String s) {
        T result = null;
        try {
            JSONObject jsonObject = new JSONObject(s);
            result = parseSingle(c, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
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

    protected void postJson(
            String path, HashMap<String, String> payload, TentoResponse.Callback callback) {
        String json = null;
        try {
            json = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            json = "{}";
        }
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        makePost(buildURI(path).toString(), body, callback);
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

        makePost(buildURI(path).toString(), formBody.build(), callback);
    }

    private void makePost(String url, RequestBody body, final TentoResponse.Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        final OkHttpClient client = new OkHttpClient();
        new AsyncTask<Request, Void, Pair<Boolean, String>>() {
            @Override
            protected Pair<Boolean, String> doInBackground(Request... requests) {
                String result = null;
                boolean success = false;
                Request request = requests[0];
                try {
                    Response response = client.newCall(request).execute();
                    success = response.isSuccessful();
                    result = response.body().string();
                } catch (IOException e) {
                    result = e.getMessage();
                }

                Pair<Boolean, String> r = new Pair<Boolean, String>(success, result);
                return r;
            }

            @Override
            protected void onPostExecute(Pair<Boolean, String> result) {
                if(!result.first) {
                    callback.fail(result.second);
                } else {
                    callback.ok(result.second);
                }
            }
        }.execute(request);
    }
}
