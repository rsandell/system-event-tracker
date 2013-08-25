package net.joinedminds.tools.evet;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.*;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static net.joinedminds.tools.evet.Functions.isEmpty;

/**
 * Description
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Singleton
public class Db {
    public static final List<String> RESERVED_NAMES = ImmutableList.of("system", "_id", "durationEvent", "start",
            "title", "node", "description", "tags", "classname", "caption");
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

    public void updateEventEnd(String id, String title, String description, String[] tags, Map<String, String> extra) {
        updateEventEnd(id, null, title, description, tags, extra);
    }

    /**
     * Updates a started event with the end timestamp.
     *
     * @param id the id, required.
     * @param end the timestamp when the event ended.
     * @param title optional if the title should be updated.
     * @param description optional id the description should be updated.
     * @param tags optional if any tags should be added
     * @param extra optional any extra properties.
     */
    public void updateEventEnd(String id, Date end, String title, String description, String[] tags, Map<String, String> extra) {
        Preconditions.checkNotNull(id, "id");
        if (end == null) {
            end = new Date();
        }
        ObjectId objectId = new ObjectId(id);
        DBObject obj = collection.findOne(objectId);
        obj.put("end", end);
        if (!isEmpty(title)) {
            obj.put("title", title);
        }
        if(!isEmpty(description)) {
            obj.put("description", description);
        }
        if(!isEmpty(tags)) {
            String[] t = (String[])obj.get("tags");
            if (t != null) {
                tags = ObjectArrays.concat(tags, t, String.class);
            }
            obj.put("tags", tags);
        }
        setClassNameAndCaption(obj, (String)obj.get("system"), (String)obj.get("node"), tags);
        if (extra != null && extra.size() > 0) {
            for(String key: extra.keySet()) {
                obj.put(key, extra.get(key));
            }
        }
        collection.save(obj);
    }

    public String addBeginEvent(String id, String system, String title, String node, String description, String[] tags, Map<String, String> extra)  {
        return addBeginEvent(id, system, new Date(), title, node, description, tags, extra);
    }

    public String addBeginEvent(String id, String system, Date start, String title, String node, String description,
                                String[] tags, Map<String, String> extra) {
        if (start == null) {
            start = new Date();
        }

        BasicDBObject obj = new BasicDBObject("start", start);
        if (!isEmpty(id)) {
            obj.append("_id", new ObjectId(id));
        }
        obj.append("durationEvent", true);

        return insertStandardEventData(obj, system, title, node, description, tags, extra);
    }

    public String addEvent(String system, String title, String node, String description, String[] tags, Map<String, String> extra) {
        return addEvent(system, new Date(), title, node, description, tags, extra);
    }

    public String addEvent(String system, Date start, String title, String node, String description, String[] tags, Map<String, String> extra) {
        Preconditions.checkNotNull(start, "start");
        BasicDBObject obj = new BasicDBObject("start", start);
        obj.append("durationEvent", false);
        return insertStandardEventData(obj, system, title, node, description, tags, extra);
    }

    private String insertStandardEventData(BasicDBObject obj, String system, String title, String node, String description, String[] tags, Map<String, String> extra) {
        Preconditions.checkNotNull(system, "system");
        obj.append("system", system);
        Preconditions.checkNotNull(title, "title");
        obj.append("title", title);
        Preconditions.checkNotNull(node, "node");
        obj.append("node", node);

        if (!isEmpty(description)) {
            obj.append("description", description);
        }
        if (!isEmpty(tags)) {
            obj.append("tags", tags);
        }
        setClassNameAndCaption(obj, system, node, tags);
        //Extra properties provided?
        if(extra != null && extra.size() > 0) {
            for (String key : extra.keySet()) {
                obj.append(key, extra.get(key));
            }
        }
        collection.insert(obj);
        ObjectId id = obj.getObjectId("_id");
        if (id != null) {
            return id.toString();
        }
        return null;
    }

    private void setClassNameAndCaption(DBObject obj, String system, String node, String[] tags) {
        StringBuilder className = new StringBuilder(system);
        className.append(" ").append(node);
        StringBuilder caption = new StringBuilder("System: ").append(system).append("\n");
        caption.append("Node: ").append(node).append("\n");
        if (!isEmpty(tags)) {
            caption.append("Tags: [");
            boolean first = true;
            for(String tag : tags) {
                if (first) {
                    caption.append(tag);
                    first = false;
                } else {
                    caption.append(",").append(tag);
                }
                className.append(" ").append(tag);
            }
            caption.append("]").append("\n");
        }
        obj.put("classname", className.toString());
        obj.put("caption", caption.toString());
    }

}
