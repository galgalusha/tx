package gal.jooq;

import gal.connection.MyConnectionFactory;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.ThreadLocalTransactionProvider;
import org.jooq.lambda.Unchecked;


public class JooqConfig {

    private static ClosableConnectionProvider previousConnectionProvider;

    public static DSLContext create() {
        return create(alwaysAllocatingNewConnections());
    }

    public static DSLContext create(ClosableConnectionProvider connectionProvider) {
        closePreviousConnectionProvider();
        previousConnectionProvider = connectionProvider;
        DefaultConfiguration conf = new DefaultConfiguration();
        conf.setSQLDialect(SQLDialect.MYSQL);
        // Don't need to do this: conf.setConnectionProvider(connectionProvider);
        conf.setTransactionProvider(new ThreadLocalTransactionProvider(connectionProvider));
        return DSL.using(conf);
    }

    public static AlwaysAllocateNewConnection alwaysAllocatingNewConnections() {
        return new AlwaysAllocateNewConnection(MyConnectionFactory::newConnection);
    }

    private static void closePreviousConnectionProvider() {
        if (previousConnectionProvider != null) {
            Unchecked.runnable(previousConnectionProvider::close).run();
        }
    }

}
