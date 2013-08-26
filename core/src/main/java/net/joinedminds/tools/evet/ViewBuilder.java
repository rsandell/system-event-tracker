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

import com.google.common.base.Splitter;
import org.kohsuke.stapler.StaplerResponse;
import org.koshuke.stapler.simile.timeline.TimelineEventList;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Calendar;
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

    public ViewBuilder getStart(String timestamp) {
        start = DatatypeConverter.parseDateTime(timestamp);
        ensureStart();
        return this;
    }

    public ViewBuilder getStartHours(String number) {
        ensureStartNow();
        start.add(Calendar.HOUR_OF_DAY, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getStartDays(String number) {
        ensureStartNow();
        start.add(Calendar.DAY_OF_YEAR, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getStartWeeks(String number) {
        ensureStartNow();
        start.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getStartMonths(String number) {
        ensureStartNow();
        start.add(Calendar.MONTH, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getStartYears(String number) {
        ensureStartNow();
        start.add(Calendar.YEAR, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getEnd(String timestamp) {
        end = DatatypeConverter.parseDateTime(timestamp);
        ensureEnd();
        return this;
    }

    public ViewBuilder getEndHours(String number) {
        ensureEnd();
        end.add(Calendar.HOUR_OF_DAY, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getEndDays(String number) {
        ensureEnd();
        end.add(Calendar.DAY_OF_YEAR, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getEndWeeks(String number) {
        ensureEnd();
        end.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getEndMonths(String number) {
        ensureEnd();
        end.add(Calendar.MONTH, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getEndYears(String number) {
        ensureEnd();
        end.add(Calendar.YEAR, Integer.parseInt(number));
        return this;
    }

    public ViewBuilder getSystems(String names) {
        if (this.systems == null) {
            this.systems = new LinkedHashSet<>();
        }
        for (String name : Splitter.on(',').on(' ').omitEmptyStrings().trimResults().split(names)) {
            systems.add(name);
        }
        return this;
    }

    public ViewBuilder getTags(String names) {
        if (this.tags == null) {
            this.tags = new LinkedHashSet<>();
        }
        for (String name : Splitter.on(',').on(' ').omitEmptyStrings().trimResults().split(names)) {
            tags.add(name);
        }
        return this;
    }

    public ViewBuilder getNodes(String names) {
        if (this.nodes == null) {
            this.nodes = new LinkedHashSet<>();
        }
        for (String name : Splitter.on(',').on(' ').omitEmptyStrings().trimResults().split(names)) {
            nodes.add(name);
        }
        return this;
    }

    public TimelineEventList doJson(StaplerResponse response) throws IOException {
        ensureStart();
        ensureEnd();
        return db.findEvents(start, end, systems, tags, nodes);
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
