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

import static net.joinedminds.tools.evet.Functions.ifNull;

/**
 * Builder for displaying a time line view.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class ViewBuilder {
    public static final int DEFAULT_START_DAYS = -2;
    protected final Db db;
    protected Calendar start;
    protected Calendar end;
    protected Set<String> systems;
    protected Set<String> tags;
    protected Set<String> nodes;
    protected FormData formData;


    public ViewBuilder(Db db) {
        this.db = db;
        formData = new FormData();
    }

    public String startString() {
        if (start == null) {
            return DatatypeConverter.printDateTime(defaultStart());
        }
        return DatatypeConverter.printDateTime(start);
    }

    public String endString() {
        if (end == null) {
            return DatatypeConverter.printDateTime(defaultEnd());
        }
        return DatatypeConverter.printDateTime(end);
    }

    public ViewBuilder getStart(String timestamp) {
        start = DatatypeConverter.parseDateTime(timestamp);
        formData.start = (Calendar)start.clone();
        ensureStart();
        return this;
    }

    public ViewBuilder getStartHours(String number) {
        ensureStartNow();
        int amount = Integer.parseInt(number);
        start.add(Calendar.HOUR_OF_DAY, amount);
        formData.startHours += amount;
        return this;
    }

    public ViewBuilder getStartDays(String number) {
        ensureStartNow();
        int amount = Integer.parseInt(number);
        start.add(Calendar.DAY_OF_YEAR, amount);
        formData.startDays += amount;
        return this;
    }

    public ViewBuilder getStartWeeks(String number) {
        ensureStartNow();
        int amount = Integer.parseInt(number);
        start.add(Calendar.WEEK_OF_YEAR, amount);
        formData.startWeeks += amount;
        return this;
    }

    public ViewBuilder getStartMonths(String number) {
        ensureStartNow();
        int amount = Integer.parseInt(number);
        start.add(Calendar.MONTH, amount);
        formData.startMonths += amount;
        return this;
    }

    public ViewBuilder getStartYears(String number) {
        ensureStartNow();
        int amount = Integer.parseInt(number);
        start.add(Calendar.YEAR, amount);
        formData.startYears += amount;
        return this;
    }

    public ViewBuilder getEnd(String timestamp) {
        end = DatatypeConverter.parseDateTime(timestamp);
        formData.end = (Calendar)end.clone();
        ensureEnd();
        return this;
    }

    public ViewBuilder getEndHours(String number) {
        ensureEnd();
        int amount = Integer.parseInt(number);
        end.add(Calendar.HOUR_OF_DAY, amount);
        formData.endHours += amount;
        return this;
    }

    public ViewBuilder getEndDays(String number) {
        ensureEnd();
        int amount = Integer.parseInt(number);
        end.add(Calendar.DAY_OF_YEAR, amount);
        formData.endDays += amount;
        return this;
    }

    public ViewBuilder getEndWeeks(String number) {
        ensureEnd();
        int amount = Integer.parseInt(number);
        end.add(Calendar.WEEK_OF_YEAR, amount);
        formData.endWeeks += amount;
        return this;
    }

    public ViewBuilder getEndMonths(String number) {
        ensureEnd();
        int amount = Integer.parseInt(number);
        end.add(Calendar.MONTH, amount);
        formData.endMonths += amount;
        return this;
    }

    public ViewBuilder getEndYears(String number) {
        ensureEnd();
        int amount = Integer.parseInt(number);
        end.add(Calendar.YEAR, amount);
        formData.endYears += amount;
        return this;
    }

    public ViewBuilder getSystems(String names) {
        if (this.systems == null) {
            this.systems = new LinkedHashSet<>();
        }
        for (String name : Splitter.on(',').omitEmptyStrings().trimResults().split(names)) {
            systems.add(name);
        }
        return this;
    }

    public ViewBuilder getTags(String names) {
        if (this.tags == null) {
            this.tags = new LinkedHashSet<>();
        }
        for (String name : Splitter.on(',').omitEmptyStrings().trimResults().split(names)) {
            tags.add(name);
        }
        return this;
    }

    public ViewBuilder getNodes(String names) {
        if (this.nodes == null) {
            this.nodes = new LinkedHashSet<>();
        }
        for (String name : Splitter.on(',').omitEmptyStrings().trimResults().split(names)) {
            nodes.add(name);
        }
        return this;
    }



    protected void ensureEnd() {
        if (end == null) {
            end = defaultEnd();
            formData.end = (Calendar)end.clone();
        }
    }

    private Calendar defaultEnd() {
        return Calendar.getInstance();
    }

    protected void ensureStart() {
        if (start == null) {
            start = defaultStart();
            formData.start = (Calendar)start.clone();
        }
    }

    public Calendar defaultStart() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, DEFAULT_START_DAYS);
        return c;
    }

    protected void ensureStartNow() {
        if (start == null) {
            start = Calendar.getInstance();
            formData.start = (Calendar)start.clone();
        }
    }

    /**
     * A class to keep track of the user request to show in the form.
     */
    static class FormData {
        Calendar start;
        public int startHours = 0;
        public int startDays = 0;
        public int startWeeks = 0;
        public int startMonths = 0;
        public int startYears = 0;
        Calendar end;
        public int endHours = 0;
        public int endDays = 0;
        public int endWeeks = 0;
        public int endMonths = 0;
        public int endYears = 0;

        public String startString() {
            if (start == null) {
                return "";
            }
            return DatatypeConverter.printDateTime(start);
        }

        public String endString() {
            if (end == null) {
                return "";
            }
            return DatatypeConverter.printDateTime(end);
        }
    }
}
