package gal;

import gal.jooq.JooqConfig;
import org.jooq.DSLContext;
import java.sql.Connection;
import java.time.Duration;
import static gal.connection.MyConnectionFactory.newConnection;
import static gal.thread.Utils.print;
import static gal.thread.Utils.startAfter;


public class Demo_3_JOOQ_Concurrent_Increase_X {

    private static DSLContext jooq = JooqConfig.create();


    public static void main(String[] args) throws Exception {


        Connection conn = newConnection();

        conn.prepareStatement("DROP TABLE IF EXISTS test;").execute();
        conn.prepareStatement("CREATE TABLE test (x INT(6) UNSIGNED)").execute();
        conn.prepareStatement("INSERT INTO test VALUES (1)").execute();

        conn.close();

        startAfter(Duration.ofMillis(100), () -> jooq.transaction(tx -> increaseX(jooq)));
        startAfter(Duration.ofMillis(500), () -> jooq.transaction(tx -> increaseX(jooq)));

    }

    private static void increaseX(DSLContext jooq) throws Exception {
        int x = jooq.fetchOne("SELECT x FROM test").get("x", Integer.class);
        print("Current value: x = " + x);
        Thread.sleep(1000);
        jooq.execute("UPDATE test SET test.x=" + (x + 1));
        x = jooq.fetchOne("SELECT x FROM test").get("x", Integer.class);
        print("Current value: x = " + x);
    }


}
