/*
 * The MIT License
 *
 * Copyright (c) 2013, Robert Sandell - sandell.robert@gmail.com. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.joinedminds.tools.evet;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.koshuke.stapler.simile.timeline.TimelineEventList;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

import static net.joinedminds.tools.evet.Functions.cssSanitize;
import static net.joinedminds.tools.evet.Functions.isEmpty;

/**
 * The database.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Singleton
public class Db {
    public static final String SYSTEM = "system";
    public static final String TAGS = "tags";
    public static final String START = "start";
    public static final String COLLECTION_NAME = "events";
    public static final String END = "end";

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String NODE = "node";
    public static final String ID = "_id";
    public static final String DURATION_EVENT = "durationEvent";
    public static final String CLASSNAME = "classname";
    public static final String CAPTION = "caption";
    public static final List<String> RESERVED_NAMES = ImmutableList.of(SYSTEM, ID, DURATION_EVENT, START,
            END, TITLE, NODE, DESCRIPTION, TAGS, CLASSNAME, CAPTION, "id");

    private final DBCollection collection;
    private String dbHost;
    private Provider<Integer> dbPort;
    private String dbName;
    private String dbUser;
    private String dbPasswd;
    private final MongoClient mongo;
    private final DB db;

    @Inject
    public Db(@Named("DB_HOST") String dbHost, @Named("DB_PORT") Provider<Integer> dbPort, @Named("DB_NAME") String dbName,
              @Named("DB_USER") String dbUser, @Named("DB_PASSWD") String dbPasswd) throws UnknownHostException {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPasswd = dbPasswd;
        Preconditions.checkNotNull(dbHost, "Missing host");
        Preconditions.checkNotNull(dbName, "Missing database name!");
        StringBuilder connStr = new StringBuilder("mongodb://");
        if (!isEmpty(dbUser)) {
            connStr.append(dbUser).append(":").append(dbPasswd);
        }
        connStr.append(dbHost);
        if (dbPort.get() != null) {
            connStr.append(":").append(dbPort.get());
        }
        connStr.append("/").append(dbName);

        MongoClientURI uri = new MongoClientURI(connStr.toString());
        mongo = new MongoClient(uri);
        db = mongo.getDB(dbName);
        collection = db.getCollection(COLLECTION_NAME);
        collection.ensureIndex(SYSTEM);
        collection.ensureIndex(TAGS);
        collection.ensureIndex(START);
        collection.ensureIndex(END);
    }

    public void updateEventEnd(String id, String title, String description, String[] tags, Map<String, String> extra) {
        updateEventEnd(id, null, title, description, tags, extra);
    }

    /**
     * Updates a started event with the end timestamp.
     *
     * @param id          the id, required.
     * @param end         the timestamp when the event ended.
     * @param title       optional if the title should be updated.
     * @param description optional id the description should be updated.
     * @param tags        optional if any tags should be added
     * @param extra       optional any extra properties.
     */
    public void updateEventEnd(String id, Date end, String title, String description, String[] tags, Map<String, String> extra) {
        Preconditions.checkNotNull(id, "id");
        if (end == null) {
            end = new Date();
        }
        ObjectId objectId = new ObjectId(id);
        DBObject obj = collection.findOne(objectId);
        obj.put(Db.END, end);
        if (!isEmpty(title)) {
            obj.put(TITLE, title);
        }
        if (!isEmpty(description)) {
            obj.put(DESCRIPTION, description);
        }
        if (!isEmpty(tags)) {
            BasicDBList list = (BasicDBList)obj.get(TAGS);
            if (list != null) {
                Set<String> ts = new HashSet<>();
                for (Object o : list) {
                    ts.add(o.toString());
                }
                Collections.addAll(ts, tags);
                tags = ts.toArray(new String[ts.size()]);
            }
            obj.put(TAGS, tags);
        } else {
            BasicDBList list = (BasicDBList)obj.get(TAGS);
            if (list != null) {
                tags = list.toArray(new String[list.size()]);
            } else {
                tags = new String[0];
            }
        }
        setClassNameAndCaption(obj, (String)obj.get(SYSTEM), (String)obj.get(NODE), tags);
        if (extra != null && extra.size() > 0) {
            for (String key : extra.keySet()) {
                obj.put(key, extra.get(key));
            }
        }
        collection.save(obj);
    }

    public String addBeginEvent(String id, String system, String title, String node, String description, String[] tags, Map<String, String> extra) {
        return addBeginEvent(id, system, new Date(), title, node, description, tags, extra);
    }

    public String addBeginEvent(String id, String system, Date start, String title, String node, String description,
                                String[] tags, Map<String, String> extra) {
        if (start == null) {
            start = new Date();
        }

        BasicDBObject obj = new BasicDBObject(Db.START, start);
        if (!isEmpty(id)) {
            obj.append(ID, new ObjectId(id));
        }
        obj.append(DURATION_EVENT, true);

        return insertStandardEventData(obj, system, title, node, description, tags, extra);
    }

    public String addEvent(String system, String title, String node, String description, String[] tags, Map<String, String> extra) {
        return addEvent(system, new Date(), title, node, description, tags, extra);
    }

    public String addEvent(String system, Date start, String title, String node, String description, String[] tags, Map<String, String> extra) {
        Preconditions.checkNotNull(start, START);
        BasicDBObject obj = new BasicDBObject(START, start);
        obj.append(DURATION_EVENT, false);
        return insertStandardEventData(obj, system, title, node, description, tags, extra);
    }

    public String addHistoricalEvent(String system, Date start, Date end, String title, String node, String description, String[] tags, Map<String, String> extra) {
        Preconditions.checkNotNull(start, START);
        BasicDBObject obj = new BasicDBObject(START, start);
        if (end != null) {
            obj.append(END, end);
            obj.append(DURATION_EVENT, true);
        } else {
            obj.append(DURATION_EVENT, false);
        }
        return insertStandardEventData(obj, system, title, node, description, tags, extra);
    }

    private String insertStandardEventData(BasicDBObject obj, String system, String title, String node, String description, String[] tags, Map<String, String> extra) {
        Preconditions.checkNotNull(system, SYSTEM);
        obj.append(SYSTEM, system);
        Preconditions.checkNotNull(title, TITLE);
        obj.append(TITLE, title);
        Preconditions.checkNotNull(node, NODE);
        obj.append(NODE, node);

        if (!isEmpty(description)) {
            obj.append(DESCRIPTION, description);
        }
        if (!isEmpty(tags)) {
            obj.append(TAGS, tags);
        }
        setClassNameAndCaption(obj, system, node, tags);
        //Extra properties provided?
        if (extra != null && extra.size() > 0) {
            for (String key : extra.keySet()) {
                obj.append(key, extra.get(key));
            }
        }
        collection.insert(obj);
        ObjectId id = obj.getObjectId(ID);
        if (id != null) {
            return id.toString();
        }
        return null;
    }

    private void setClassNameAndCaption(DBObject obj, String system, String node, String[] tags) {
        StringBuilder className = new StringBuilder(cssSanitize(system));
        className.append(" ").append(cssSanitize(node));
        StringBuilder caption = new StringBuilder("System: ").append(system).append("\n");
        caption.append("Node: ").append(node).append("\n");
        if (!isEmpty(tags)) {
            caption.append("Tags: [");
            boolean first = true;
            for (String tag : tags) {
                if (first) {
                    caption.append(tag);
                    first = false;
                } else {
                    caption.append(",").append(tag);
                }
                className.append(" ").append(cssSanitize(tag));
            }
            caption.append("]").append("\n");
        }
        obj.put(CLASSNAME, className.toString());
        obj.put(CAPTION, caption.toString());
    }


    /**
     * Searches the database and returns the result.
     *
     * @param start
     * @param end
     * @param systems
     * @param tags
     * @param nodes
     */
    public TimelineEventList findEvents(Calendar start, Calendar end, Set<String> systems, Set<String> tags, Set<String> nodes) throws IOException {
        Preconditions.checkNotNull(start, START);
        Preconditions.checkNotNull(end, END);
        String[] systemsArr = null;
        String[] nodesArr = null;
        String[] tagsArr = null;

        if (!isEmpty(systems)) {
            systemsArr = systems.toArray(new String[systems.size()]);
        }
        if (!isEmpty(nodes)) {
            nodesArr = nodes.toArray(new String[nodes.size()]);
        }
        if (!isEmpty(tags)) {
            tagsArr = tags.toArray(new String[tags.size()]);
        }

        Set<DbEvent> events = Sets.newHashSet();
        for (DBObject timeSearch : timeSearches(start, end)) {
            BasicDBObject search = new BasicDBObject();

            search.putAll(timeSearch);
            if (systemsArr != null) {
                search.put(SYSTEM, new BasicDBObject("$in", systemsArr));
            }
            if (nodesArr != null) {
                search.put(NODE, new BasicDBObject("$in", nodesArr));
            }
            if (tagsArr != null) {
                search.put(TAGS, new BasicDBObject("$in", tagsArr));
            }

            DBCursor cursor = collection.find(search);

            while (cursor.hasNext()) {
                DBObject o = cursor.next();
                events.add(new DbEvent(o));

            }
        }
        TimelineEventList list = new TimelineEventList();
        list.addAll(events);
        return list;

    }

    public TimeValueTable countEvents(Calendar start, Calendar end, Set<String> systems, Set<String> tags, Set<String> nodes) {
        Preconditions.checkNotNull(start, START);
        Preconditions.checkNotNull(end, END);
        String[] systemsArr = null;
        String[] nodesArr = null;
        String[] tagsArr = null;

        if (!isEmpty(systems)) {
            systemsArr = systems.toArray(new String[systems.size()]);
        }
        if (!isEmpty(nodes)) {
            nodesArr = nodes.toArray(new String[nodes.size()]);
        }
        if (!isEmpty(tags)) {
            tagsArr = tags.toArray(new String[tags.size()]);
        }

        List<DBObject> times = timeSearches(start, end);
        QueryBuilder builder = QueryBuilder.start().or(times.toArray(new DBObject[times.size()]));

        if (systemsArr != null) {
            builder = builder.and(SYSTEM).in(systemsArr);
        }
        if (nodesArr != null) {
            builder = builder.and(NODE).in(nodesArr);
        }
        if (tagsArr != null) {
            builder = builder.and(TAGS).in(tagsArr);
        }

        BasicDBObject modify = new BasicDBObject();
        modify.put("startYear", new BasicDBObject("$year", "$start"));
        modify.put("startMonth", new BasicDBObject("$month", "$start"));
        modify.put("startDay", new BasicDBObject("$dayOfMonth", "$start"));

        BasicDBObject groupNames = new BasicDBObject();
        groupNames.put("startYear", "$startYear");
        groupNames.put("startMonth", "$startMonth");
        groupNames.put("startDay", "$startDay");

        BasicDBObject group = new BasicDBObject("_id", groupNames);
        group.put("total", new BasicDBObject("$sum", 1));

        AggregationOutput output = collection.aggregate(
                new BasicDBObject("$match", builder.get()),
                new BasicDBObject("$project", modify),
                new BasicDBObject("$group", group));

        TimeValueTable result = new TimeValueTable();

        for (DBObject obj: output.results()) {
            DBObject id = (DBObject) obj.get("_id");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(0);
            c.set(Calendar.YEAR, (Integer) id.get("startYear"));
            c.set(Calendar.MONTH, (Integer) id.get("startMonth"));
            c.set(Calendar.DAY_OF_MONTH, (Integer) id.get("startDay"));
            result.put(c.getTime(), (Integer) obj.get("total"));
        }
        return result;
    }

    private List<DBObject> timeSearches(Calendar start, Calendar end) {
        return ImmutableList.of(
                oneShotEventsSearch(start, end),
                durationEventsStartedWithin(start, end),
                durationEventEndedWithin(start, end),
                durationEventRunningThrough(start, end));
    }

    private DBObject oneShotEventsSearch(Calendar start, Calendar end) {
        return QueryBuilder.start(DURATION_EVENT).is(false).
                and(START).greaterThanEquals(start.getTime()).
                and(START).lessThanEquals(end.getTime()).get();
    }

    private DBObject durationEventsStartedWithin(Calendar start, Calendar end) {
        return QueryBuilder.start(DURATION_EVENT).is(true).
                and(START).greaterThanEquals(start.getTime()).
                and(START).lessThanEquals(end.getTime()).get();
    }

    private DBObject durationEventEndedWithin(Calendar start, Calendar end) {
        return QueryBuilder.start(DURATION_EVENT).is(true).
                and(END).greaterThanEquals(start.getTime()).
                and(END).lessThanEquals(end.getTime()).get();
    }

    private DBObject durationEventRunningThrough(Calendar start, Calendar end) {
        return QueryBuilder.start(DURATION_EVENT).is(true).
                and(START).lessThan(start.getTime()).
                and(END).greaterThan(end.getTime()).get();
    }
}
