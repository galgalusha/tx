package gal;

import gal.jooq.JooqConfig;
import org.jooq.DSLContext;
import org.jooq.lambda.fi.lang.CheckedRunnable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Duration;
import static gal.connection.MyConnectionFactory.newConnection;


public class Main {

    static DSLContext jooq = JooqConfig.create();

    /**
     * MySQL commands to view locks:
     * SHOW FULL PROCESSLIST;
     * SHOW OPEN TABLES WHERE In_Use > 0 ;
     */
    public static void main(String[] args) throws Exception {


        Connection conn = newConnection();

        conn.prepareStatement("DROP TABLE IF EXISTS test;").execute();
        conn.prepareStatement("CREATE TABLE test (x INT(6) UNSIGNED)").execute();
        conn.prepareStatement("INSERT INTO test VALUES (1)").execute();

        conn.close();

//        startAfter(Duration.ofMillis(500), () -> increaseX(newConnection()));
//        startAfter(Duration.ofMillis(100), () -> increaseX(newConnection()));

        startAfter(Duration.ofMillis(500), () -> increaseX(jooq));
        startAfter(Duration.ofMillis(100), () -> increaseX(jooq));

//        startAfter(Duration.ofMillis(500), () -> jooq.transaction(tx -> increaseX(jooq)));
//        startAfter(Duration.ofMillis(100), () -> jooq.transaction(tx -> increaseX(jooq)));


        print("ok");
    }

    private static void increaseX(DSLContext jooq) throws Exception {
        int x = jooq.fetchOne("SELECT x FROM test FOR UPDATE").get("x", Integer.class);
        print("Current value: x = " + x);
        Thread.sleep(1000);
        jooq.execute("UPDATE test SET test.x=" + (x + 1));
        x = jooq.fetchOne("SELECT x FROM test").get("x", Integer.class);
        print("Current value: x = " + x);
    }

    private static void increaseX(Connection conn) throws Exception {
        int x = readInt(conn, "SELECT x FROM test FOR UPDATE");
        print("Current value: x = " + x);
        Thread.sleep(1000);
        conn.prepareStatement("UPDATE test SET test.x=" + (x + 1)).execute();
        x = readInt(conn, "SELECT x FROM test FOR UPDATE");
        print("Current value: x = " + x);
    }

    private static int readInt(Connection conn, String sql) throws Exception {
        try (ResultSet resultSet = conn.prepareStatement(sql).executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private static void print(String s) {
        System.out.println("[Thread \"" + Thread.currentThread().getName() + "\"]: " + s);
    }

    private static void startAfter(Duration delay, CheckedRunnable action) {
        new Thread(() -> {
            try {
                Thread.sleep(delay.toMillis());
                action.run();
            } catch (Throwable e) { throw new RuntimeException(e); }
        }).start();
    }

}
