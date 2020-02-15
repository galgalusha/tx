package gal;

import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.time.Duration;

import static gal.connection.MyConnectionFactory.newConnection;
import static gal.thread.Utils.print;
import static gal.thread.Utils.startAfter;

@Service("Demo_4_Spring_demo")
public class Demo_4_Spring_demo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager txManager;

    public void run() throws Exception {

        Connection conn = newConnection();

        conn.prepareStatement("DROP TABLE IF EXISTS test;").execute();
        conn.prepareStatement("CREATE TABLE test (x INT(6) UNSIGNED)").execute();
        conn.prepareStatement("INSERT INTO test VALUES (1)").execute();

        conn.close();

        startAfter(Duration.ofMillis(100), () -> new TransactionTemplate(txManager).execute(this::increaseX));
        startAfter(Duration.ofMillis(500), () -> new TransactionTemplate(txManager).execute(this::increaseX));
    }

    // @Transactional
    public Object increaseX(TransactionStatus _ignore_me_) {
        int x = jdbcTemplate.queryForObject("SELECT x FROM test FOR UPDATE", Integer.class);
        print("Current value: x = " + x);
        Unchecked.runnable(() -> Thread.sleep(1000)).run();
        jdbcTemplate.update("UPDATE test SET test.x=" + (x + 1));
        x = jdbcTemplate.queryForObject("SELECT x FROM test FOR UPDATE", Integer.class);
        print("Current value: x = " + x);
        return null;
    }
}
