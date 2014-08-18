package kr.tento.api;

import java.util.HashMap;

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
                callback.fail(error);
            }
        });
    }
}
