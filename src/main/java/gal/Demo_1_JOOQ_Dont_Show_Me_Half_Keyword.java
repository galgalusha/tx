package gal;

import gal.jooq.JooqConfig;
import org.jooq.DSLContext;
import org.jooq.Record;
import java.sql.Connection;
import java.time.Duration;
import java.util.stream.Stream;
import static gal.connection.MyConnectionFactory.newConnection;
import static gal.thread.Utils.print;
import static gal.thread.Utils.startAfter;


public class Demo_1_JOOQ_Dont_Show_Me_Half_Keyword {

    private static DSLContext jooq = JooqConfig.create();

    public static void main(String[] args) throws Exception {


        Connection conn = newConnection();

        conn.prepareStatement("DROP TABLE IF EXISTS keywords;").execute();
        conn.prepareStatement("DROP TABLE IF EXISTS urls;").execute();
        conn.prepareStatement("CREATE TABLE keywords (id INT(6) UNSIGNED)").execute();
        conn.prepareStatement("CREATE TABLE urls ( keyword_id INT(6) UNSIGNED, url VARCHAR(100) )").execute();

        conn.close();
/*
        startAfter(Duration.ofMillis(100), () -> createKeywordWithURL(jooq));
        startAfter(Duration.ofMillis(500), () -> uploadAllKeywords(jooq));
        startAfter(Duration.ofMillis(1500), () -> uploadAllKeywords(jooq));
*/
        startAfter(Duration.ofMillis(100), () -> jooq.transaction(tx -> createKeywordWithURL(jooq)));
        startAfter(Duration.ofMillis(500), () -> jooq.transaction(tx -> uploadAllKeywords(jooq)));
        startAfter(Duration.ofMillis(1500), () -> jooq.transaction(tx -> uploadAllKeywords(jooq)));

        print("ok");
    }

    private static void createKeywordWithURL(DSLContext jooq) throws Exception {
        jooq.execute("INSERT INTO keywords VALUES (1)");
        print("Inserted keyword with id=1");
        Thread.sleep(1000);
        jooq.execute("INSERT INTO urls VALUES ( 1, 'www.gal.com' )");
        print("Inserted URL with keyword_id=1");
    }

    private static void uploadAllKeywords(DSLContext jooq) throws Exception {
        try (Stream<Record> keywords = jooq.fetchStream("SELECT keywords.id, urls.url FROM keywords LEFT JOIN urls on keywords.id=urls.keyword_id")) {
            keywords.forEach(keyword -> print("uploading Keyword: Id=" + keyword.get("id") + ", URL=" + keyword.get("url")));
        }
    }

}
