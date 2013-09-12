/*
 * The MIT License
 *
 * Copyright (c) 2013, Sony Mobile Communications AB. All rights reserved.
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

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import org.koshuke.stapler.simile.timeline.Event;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static net.joinedminds.tools.evet.Db.CAPTION;
import static net.joinedminds.tools.evet.Db.CLASSNAME;
import static net.joinedminds.tools.evet.Db.DESCRIPTION;
import static net.joinedminds.tools.evet.Db.DURATION_EVENT;
import static net.joinedminds.tools.evet.Db.END;
import static net.joinedminds.tools.evet.Db.ID;
import static net.joinedminds.tools.evet.Db.NODE;
import static net.joinedminds.tools.evet.Db.START;
import static net.joinedminds.tools.evet.Db.SYSTEM;
import static net.joinedminds.tools.evet.Db.TAGS;
import static net.joinedminds.tools.evet.Db.TITLE;

/**
 * @author Robert Sandell
 */
public class DbEvent extends Event implements Map<String, Object> {

    public static final String COLOR = "color";
    public static final String LINK = "link";
    public String id;
    public String system;
    public String[] tags;
    public String node;
    public String caption;
    private Map<String, Object> map;

    public DbEvent(DBObject db) {
        map = new HashMap<>();
        this.start = (Date)db.get(START);
        map.put(START, start);
        this.end = (Date)db.get(END);
        map.put(END, end);
        this.durationEvent = (Boolean)db.get(DURATION_EVENT);
        map.put(DURATION_EVENT, durationEvent);
        this.title = (String)db.get(TITLE);
        map.put(TITLE, title);
        this.classname = (String)db.get(CLASSNAME);
        map.put(CLASSNAME, classname);
        this.color = (String)db.get(COLOR);
        this.description = (String)db.get(DESCRIPTION);
        map.put(DESCRIPTION, description);
        this.link = (String)db.get(LINK);
        this.id = db.get(ID).toString();
        map.put("id", id);
        this.system = (String)db.get(SYSTEM);
        map.put(SYSTEM, system);
        BasicDBList tags = (BasicDBList)db.get(TAGS);
        if (tags != null) {
            this.tags = tags.toArray(new String[tags.size()]);
        }
        map.put(TAGS, tags);
        this.node = (String)db.get(NODE);
        map.put(NODE, node);
        this.caption = (String)db.get(CAPTION);
        map.put(CAPTION, caption);

        //Extra properties.
        for (String key : db.keySet()) {
            if (!Db.RESERVED_NAMES.contains(key)) {
                Object o = db.get(key);
                if (o != null) {
                    map.put(key, o.toString());
                }
            }
        }

        if (end == null && durationEvent) {
            map.put(END, new Date());
            map.put("tapeImage", Functions.getSafeRootUrl() + "/img/gray-striped.png");
            map.put("tapeRepeat", "repeat-x");
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        throw new IllegalStateException("Immutable");
    }

    @Override
    public Object remove(Object key) {
        throw new IllegalStateException("Immutable");
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new IllegalStateException("Immutable");
    }

    @Override
    public void clear() {
        throw new IllegalStateException("Immutable");
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbEvent dbEvent = (DbEvent)o;

        if (id != null ? !id.equals(dbEvent.id) : dbEvent.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
