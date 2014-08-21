package kr.tento.model;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Token {

    private String token;

    private Date expiredAt;

    public Token(JSONObject o) throws JSONException {
        token = o.getString("token");
    }
}
