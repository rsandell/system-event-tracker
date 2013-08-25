package net.joinedminds.tools.evet;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;

/**
 * Description
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Singleton
public class Db {

    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPasswd;
    private final MongoClient mongo;

    @Inject
    public Db(@Named("DB_HOST") String dbHost, @Named("DB_NAME") String dbName,
              @Named("DB_USER") String dbUser, @Named("DB_PASSWD") String dbPasswd) throws UnknownHostException {
        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPasswd = dbPasswd;
        Preconditions.checkNotNull(dbHost, "Missing host");
        Preconditions.checkNotNull(dbName, "Missing database name!");
        StringBuilder connStr = new StringBuilder("mongodb://");
        if (!Functions.isEmpty(dbUser)) {
            connStr.append(dbUser).append(":").append(dbPasswd);
        }
        connStr.append(dbHost).append("/").append(dbName);

        MongoClientURI uri = new MongoClientURI(connStr.toString());
        mongo = new MongoClient(uri);
    }
}
