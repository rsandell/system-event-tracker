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

import java.util.Date;

import static net.joinedminds.tools.evet.Db.*;

/**
 *
 * @author Robert Sandell
 */
public class DbEvent extends Event {

    public String id;
    public String system;
    public String[] tags;
    public String node;
    public String caption;

    public DbEvent(DBObject db) {
        this.start = (Date) db.get(START);
        this.end = (Date) db.get(END);
        this.durationEvent = (Boolean) db.get(DURATION_EVENT);
        this.title = (String) db.get(TITLE);
        this.classname = (String) db.get(CLASSNAME);
        this.color = (String) db.get("color");
        this.description = (String) db.get(DESCRIPTION);
        this.link = (String) db.get(DESCRIPTION);
        this.id = db.get(ID).toString();
        this.system = (String) db.get(SYSTEM);
        BasicDBList tags = (BasicDBList) db.get(TAGS);
        if (tags != null) {
            this.tags = tags.toArray(new String[tags.size()]);
        }
        this.node = (String) db.get(NODE);
        this.caption = (String) db.get(CAPTION);
    }
}
