package kr.tento;

import org.junit.Test;

import kr.tento.api.TentoResponse;
import kr.tento.api.UserAPI;
import kr.tento.model.User;



public class UserModelTest extends ModelTestCase {

    @Test
    public void testInitializeUserModel() {
        int id = 1;
        String email = "admire9@gmail.com";
        String name = "admire9";

        User u = new User(id, email, name);

        assertEquals(email, u.getEmail());
        assertEquals(id, u.getId());
        assertEquals(name, u.getName());
    }

    @Test
    public void testWebCreateUser() throws Throwable {
        final int id = 1;
        final String email = "mytest@test.com";
        final String name = "mytest";
        final User u = new User(id, email, name);
        u.setPassword("testpassword");

        Runnable r = new Runnable() {
            @Override
            public void run() {
                UserAPI userApi = new UserAPI();
                userApi.create(u, new TentoResponse.single<User>() {
                    @Override
                    public void ok(User o) {
                        assertEquals(email, u.getEmail());
                        assertEquals(u.getId(), id);
                        assertEquals(name, u.getName());
                        testAsyncDone();
                    }

                    @Override
                    public void fail(String f) {
                        throw new AssertionError(f);
                    }
                });
            }
        };

        testAsync(r);
    }
}