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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Description
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class TimeValueTable implements ListMultimap<Date, Integer>, HttpResponse {

    public static final FastDateFormat DATE_FORMAT = DateFormatUtils.ISO_DATE_FORMAT;
    public static final FastDateFormat DATETIME_FORMAT = DateFormatUtils.ISO_DATETIME_FORMAT;
    ListMultimap<Date, Integer> internal = LinkedListMultimap.create();

    private Format format = Format.Csv;
    private Db.CountResolution resolution = Db.CountResolution.Day;

    public static enum Format {
        Csv, Json, Jsonp
    }

    /**
     * Renders HTTP response.
     */
    @Override
    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
        switch (format) {
            case Json:
                generateJsonResponse(rsp);
                break;
            case Jsonp:
                generateJsonpResponse(rsp);
                break;
            case Csv:
            default:
                generateCsvResponse(rsp);
                break;
        }
    }

    protected void generateJsonResponse(StaplerResponse rsp) throws IOException {
        rsp.setContentType("application/json");
        PrintWriter out = rsp.getWriter();
        JSONObject data = getJsonObject();
        data.write(out);
        out.flush();
    }

    protected void generateJsonpResponse(StaplerResponse rsp) throws IOException {
        rsp.setContentType("text/javascript");
        PrintWriter out = rsp.getWriter();
        out.print("evet = ");
        JSONObject data = getJsonObject();
        data.write(out);
        out.print(";");
        out.flush();
    }

    private JSONObject getJsonObject() {
        FastDateFormat fdf = resolution == Db.CountResolution.Day ? DATE_FORMAT : DATETIME_FORMAT;
        JSONObject data = new JSONObject();
        JSONArray categories = new JSONArray();
        JSONArray values = new JSONArray();
        SortedSet<Date> keys = new TreeSet<>();
        keys.addAll(keySet());
        for (Date key : keys) {
            categories.add(fdf.format(key));
            values.add(get(key).get(0)); //TODO support multisets
        }
        data.put("categories", categories);
        data.put("data", values);
        return data;
    }

    protected void generateCsvResponse(StaplerResponse rsp) throws IOException {
        FastDateFormat fdf = resolution == Db.CountResolution.Day ? DATE_FORMAT : DATETIME_FORMAT;
        rsp.setContentType("text/csv");
        PrintWriter out = rsp.getWriter();
        SortedSet<Date> keys = new TreeSet<>();
        keys.addAll(keySet());
        for (Date key : keys) {
            out.print(fdf.format(key));
            for (Integer value : get(key)) {
                out.print(',');
                out.print(value);
            }
            out.println();
        }
        out.flush();
    }

    @Override
    public int size() {
        return internal.size();
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internal.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return internal.containsValue(value);
    }

    @Override
    public boolean containsEntry(Object key, Object value) {
        return internal.containsEntry(key, value);
    }

    @Override
    public boolean put(Date key, Integer value) {
        return internal.put(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return internal.remove(key, value);
    }

    @Override
    public boolean putAll(Date key, Iterable<? extends Integer> values) {
        return internal.putAll(key, values);
    }

    @Override
    public boolean putAll(Multimap<? extends Date, ? extends Integer> multimap) {
        return internal.putAll(multimap);
    }

    @Override
    public List<Integer> replaceValues(Date key, Iterable<? extends Integer> values) {
        return internal.replaceValues(key, values);
    }

    @Override
    public List<Integer> removeAll(Object key) {
        return internal.removeAll(key);
    }

    @Override
    public void clear() {
        internal.clear();
    }

    @Override
    public List<Integer> get(Date key) {
        return internal.get(key);
    }

    @Override
    public Set<Date> keySet() {
        return internal.keySet();
    }

    @Override
    public Multiset<Date> keys() {
        return internal.keys();
    }

    @Override
    public Collection<Integer> values() {
        return internal.values();
    }

    @Override
    public Collection<Map.Entry<Date, Integer>> entries() {
        return internal.entries();
    }

    @Override
    public Map<Date, Collection<Integer>> asMap() {
        return internal.asMap();
    }

    @Override
    public boolean equals(Object obj) {
        return internal.equals(obj);
    }

    @Override
    public int hashCode() {
        return internal.hashCode();
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Db.CountResolution getResolution() {
        return resolution;
    }

    public void setResolution(Db.CountResolution resolution) {
        this.resolution = resolution;
    }
}
