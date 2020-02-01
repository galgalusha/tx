package gal.thread;

import org.jooq.lambda.fi.lang.CheckedRunnable;

import java.time.Duration;

public class Utils {

    public static void print(String s) {
        System.out.println("[Thread \"" + Thread.currentThread().getName() + "\"]: " + s);
    }

    public static void startAfter(Duration delay, CheckedRunnable action) {
        new Thread(() -> {
            try {
                Thread.sleep(delay.toMillis());
                action.run();
            } catch (Throwable e) { throw new RuntimeException(e); }
        }).start();
    }
}
