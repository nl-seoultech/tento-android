package tento.kr.tento.api;

import java.util.List;

public class TentoResponse {
    public interface Callback {
        public void ok(String result);
        public void fail(String error);
    }

    public interface single<T> {
        public void ok(T o);
        public void fail(String f);
    }

    public interface collection<T extends List> {
        public void ok(T o);
        public void fail(String f);
    }
}
