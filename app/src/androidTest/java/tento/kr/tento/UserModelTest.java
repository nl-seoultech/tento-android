package tento.kr.tento;

import android.test.ActivityUnitTestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import tento.kr.tento.model.User;



public class UserModelTest extends ActivityUnitTestCase {

    final CountDownLatch signal = new CountDownLatch(1);

    public UserModelTest(Class activityClass) {
        super(activityClass);
    }

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
    public void testCreateUser() {
        /*
        runTestOnUiThread(new Runnable() {

            @Override
            public void run() {
            }
        });
        */

        int id = 1;
        String email = "admire9@gmail.com";
        String name = "admire9";
        String password = "helloworld";

        User u = new User(id, email, name);
        u.create(password, new User.TentoCallback() {
            @Override
            public void success(String result) {
                org.junit.Assert.assertEquals("!", result);
            }

            @Override
            public void error() {
                org.junit.Assert.assertEquals("!", "@");
            }
        });
    }
}