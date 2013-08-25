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

import org.kohsuke.stapler.StaplerResponse;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Builder for displaying a time line view.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class ViewBuilder {
    public static final int DEFAULT_START_DAYS = -2;
    private final Db db;
    private Calendar start;
    private Calendar end;
    private Set<String> systems;
    private Set<String> tags;
    private Set<String> nodes;


    public ViewBuilder(Db db) {
        this.db = db;
    }

    public ViewBuilder doStart(String timestamp) {
        start = DatatypeConverter.parseDateTime(timestamp);
        ensureStart();
        return this;
    }

    public ViewBuilder doStartHours(int number) {
        ensureStartNow();
        start.add(Calendar.HOUR_OF_DAY, number);
        return this;
    }

    public ViewBuilder doStartDays(int number) {
        ensureStartNow();
        start.add(Calendar.DAY_OF_YEAR, number);
        return this;
    }

    public ViewBuilder doStartWeeks(int number) {
        ensureStartNow();
        start.add(Calendar.WEEK_OF_YEAR, number);
        return this;
    }
    public ViewBuilder doStartMonths(int number) {
        ensureStartNow();
        start.add(Calendar.MONTH, number);
        return this;
    }

    public ViewBuilder doStartYears(int number) {
        ensureStartNow();
        start.add(Calendar.YEAR, number);
        return this;
    }

    public ViewBuilder doEnd(String timestamp) {
        end = DatatypeConverter.parseDateTime(timestamp);
        ensureEnd();
        return this;
    }

    public ViewBuilder doEndHours(int number) {
        ensureEnd();
        end.add(Calendar.HOUR_OF_DAY, number);
        return this;
    }

    public ViewBuilder doEndDays(int number) {
        ensureEnd();
        end.add(Calendar.DAY_OF_YEAR, number);
        return this;
    }

    public ViewBuilder doEndWeeks(int number) {
        ensureEnd();
        end.add(Calendar.WEEK_OF_YEAR, number);
        return this;
    }
    public ViewBuilder doEndMonths(int number) {
        ensureEnd();
        end.add(Calendar.MONTH, number);
        return this;
    }

    public ViewBuilder doEndYears(int number) {
        ensureEnd();
        end.add(Calendar.YEAR, number);
        return this;
    }

    public ViewBuilder doSystems(String[] names) {
        if (this.systems == null) {
            this.systems = new LinkedHashSet<>();
        }
        Collections.addAll(systems, names);
        return this;
    }

    public ViewBuilder doTags(String[] names) {
        if (this.tags == null) {
            this.tags = new LinkedHashSet<>();
        }
        Collections.addAll(tags, names);
        return this;
    }

    public ViewBuilder doNodes(String[] names) {
        if (this.nodes == null) {
            this.nodes = new LinkedHashSet<>();
        }
        Collections.addAll(nodes, names);
        return this;
    }

    public void doJson(StaplerResponse response) throws IOException {
        ensureStart();
        ensureEnd();
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.write('{');
        writer.write("'dateTimeFormat': 'iso8601', ");
        writer.write("'events': ");
        db.findEvents(start, end, systems, tags, nodes, writer);
        writer.write(" }");
        writer.flush();
    }

    private void ensureEnd() {
        if (end == null) {
            end = Calendar.getInstance();
        }
    }

    private void ensureStart() {
        if (start == null) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, DEFAULT_START_DAYS);
            start = c;
        }
    }

    private void ensureStartNow() {
        if (start == null) {
            start = Calendar.getInstance();
        }
    }
}
