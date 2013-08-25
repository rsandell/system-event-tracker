package net.joinedminds.tools.evet;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.Date;

import static net.joinedminds.tools.evet.Functions.isEmpty;

/**
 * Description
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Singleton
public class Db {
    public static final String COLLECTION_NAME = "events";
    private final DBCollection collection;
    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPasswd;
    private final MongoClient mongo;
    private final DB db;

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
        if (!isEmpty(dbUser)) {
            connStr.append(dbUser).append(":").append(dbPasswd);
        }
        connStr.append(dbHost).append("/").append(dbName);

        MongoClientURI uri = new MongoClientURI(connStr.toString());
        mongo = new MongoClient(uri);
        db = mongo.getDB(dbName);
        collection = db.getCollection(COLLECTION_NAME);
        collection.ensureIndex("system");
        collection.ensureIndex("tags");
        collection.ensureIndex("start");
        collection.ensureIndex("end");
    }

    public String addBeginEvent(String id, String system, String title, String description, String[] tags)  {
        return addBeginEvent(id, system, new Date(), title, description, tags);
    }

    public String addBeginEvent(String id, String system, Date start, String title, String description, String[] tags) {
        BasicDBObject obj = new BasicDBObject("start", start);
        if (!isEmpty(id)) {
            obj.append("_id", new ObjectId(id));
        }
        obj.append("durationEvent", true);

        return insertStandardEventData(obj, system, title, description, tags);
    }

    public String addEvent(String system, String title, String description, String[] tags) {
        return addEvent(system, new Date(), title, description, tags);
    }

    public String addEvent(String system, Date start, String title, String description, String[] tags) {
        BasicDBObject obj = new BasicDBObject("start", start);
        obj.append("durationEvent", false);
        return insertStandardEventData(obj, system, title, description, tags);
    }

    private String insertStandardEventData(BasicDBObject obj, String system, String title, String description, String[] tags) {
        obj.append("system", system);
        StringBuilder classname = new StringBuilder(system);
        StringBuilder caption = new StringBuilder("System: ").append(system).append("\n");
        obj.append("title", title);

        if (!isEmpty(description)) {
            obj.append("description", description);
        }
        if (!isEmpty(tags)) {
            obj.append("tags", tags);
            caption.append("Tags: [");
            boolean first = true;
            for(String tag : tags) {
                if (first) {
                    caption.append(tag);
                    first = false;
                } else {
                    caption.append(",").append(tag);
                }
                classname.append(" ").append(tag);
            }
            caption.append("]").append("\n");
        }
        obj.append("classname", classname.toString());
        obj.append("caption", caption.toString());
        collection.insert(obj);
        ObjectId id = obj.getObjectId("_id");
        if (id != null) {
            return id.toString();
        }
        return null;
    }

}
