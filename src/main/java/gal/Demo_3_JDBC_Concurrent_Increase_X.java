package gal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.Duration;

import static gal.connection.MyConnectionFactory.newConnection;
import static gal.thread.Utils.print;
import static gal.thread.Utils.startAfter;


public class Demo_3_JDBC_Concurrent_Increase_X {

    private static final int INC_X_DELAY = 1000;


    public static void main(String[] args) throws Exception {


        Connection conn = newConnection();

        conn.prepareStatement("DROP TABLE IF EXISTS test;").execute();
        conn.prepareStatement("CREATE TABLE test (x INT(6) UNSIGNED)").execute();
        conn.prepareStatement("INSERT INTO test VALUES (1)").execute();

        conn.close();

        startAfter(Duration.ofMillis(100), () -> increaseX(newConnection()));
        startAfter(Duration.ofMillis(500), () -> increaseX(newConnection()));


        print("ok");
    }

    private static void increaseX(Connection conn) throws Exception {

        // TODO: start transaction

        int x = readInt(conn, "SELECT x FROM test FOR UPDATE");
        print("Current value: x = " + x);
        Thread.sleep(INC_X_DELAY);
        conn.prepareStatement("UPDATE test SET test.x=" + (x + 1)).execute();
        x = readInt(conn, "SELECT x FROM test");
        print("Current value: x = " + x);
    }

    private static int readInt(Connection conn, String sql) throws Exception {
        try (ResultSet resultSet = conn.prepareStatement(sql).executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

}
