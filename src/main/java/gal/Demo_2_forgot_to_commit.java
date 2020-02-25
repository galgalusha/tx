package gal;

import java.sql.Connection;
import java.time.Duration;

import static gal.connection.MyConnectionFactory.newConnection;
import static gal.thread.Utils.print;
import static gal.thread.Utils.startAfter;


public class Demo_2_forgot_to_commit {

    public static void main(String[] args) throws Exception {

        Connection conn = newConnection();

        conn.prepareStatement("DROP TABLE IF EXISTS test;").execute();
        conn.prepareStatement("CREATE TABLE test (x INT(6) UNSIGNED)").execute();
        conn.prepareStatement("INSERT INTO test VALUES (1)").execute();

        conn.close();

        startAfter(Duration.ofMillis(100), () -> updateX(2));
        startAfter(Duration.ofMillis(500), () -> updateX(3));

    }

    private static void updateX(int newValue) throws Exception {

        print("updating x");

        Connection conn = newConnection();

        // start transaction
        conn.setAutoCommit(false);

        conn.prepareStatement("UPDATE test SET test.x=" + newValue).execute();

        // end transaction
        // conn.commit();

        print("x updated to " + newValue);
    }

}
