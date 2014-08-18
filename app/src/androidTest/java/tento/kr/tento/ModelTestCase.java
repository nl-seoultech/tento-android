package tento.kr.tento;

import android.test.InstrumentationTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ModelTestCase extends InstrumentationTestCase {

    final CountDownLatch signal = new CountDownLatch(1);

    public void testAsync(Runnable r) throws Throwable {
        runTestOnUiThread(r);
        signal.await(30, TimeUnit.SECONDS);
    }

    public void testAsyncDone() {
        signal.countDown();
    }
}
