package kr.tento.api;

import java.util.HashMap;

import kr.tento.model.Token;
import kr.tento.model.User;

public class UserAPI extends TentoAPI<User> {

    public void create(final User user, final TentoResponse.single<User> callback) {
        HashMap<String, String> formData = new HashMap<String, String>() {
            {
                put("email", user.getEmail());
                put("password", user.getPassword());
            }
        };

        form("/users/", formData, new TentoResponse.Callback() {
            @Override
            public void ok(String result) {
                User u = parseOne(User.class, result);
                callback.ok(u);
            }

            @Override
            public void fail(String error) {
                try {
                    callback.fail(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void login(final User user, final TentoResponse.single<User> callback) {
        HashMap<String, String> formData = new HashMap<String, String>() {
            {
                put("email", user.getEmail());
                put("password", user.getPassword());
            }
        };

        form("/login/", formData, new TentoResponse.Callback() {
            @Override
            public void ok(String result) {
                User u = parseOne(User.class, result, "user");
                TentoAPI<Token> t = new TentoAPI<Token>();
                Token token = t.parseSingle(Token.class, result);
                u.setToken(token);
                callback.ok(u);
            }

            @Override
            public void fail(String error) {
                try {
                    callback.fail(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
