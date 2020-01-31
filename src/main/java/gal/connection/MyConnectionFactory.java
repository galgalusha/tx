package gal.connection;

import com.mysql.jdbc.ConnectionImpl;
import gal.jooq.JooqConfig;
import java.sql.SQLException;
import java.util.Properties;
import static java.lang.Integer.parseInt;


public class MyConnectionFactory {

    public static final String MY_PROPERTIES_FILE = "/database.properties";

    public static ConnectionImpl newConnection() {
        return newConnection(withPropertiesFrom(MY_PROPERTIES_FILE));
    }

    public static ConnectionImpl newConnection(Properties props)  {
        try {
            return new ConnectionImpl(props.getProperty("server"), parseInt(props.getProperty("port")), props, props.getProperty("database"), null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Properties withPropertiesFrom(String resourceName) {
        try {
            Properties props = new Properties();
            props.load(JooqConfig.class.getResourceAsStream(resourceName));
            return props;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
