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

import com.google.inject.Singleton;
import com.google.inject.Inject;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.framework.adjunct.AdjunctManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static net.joinedminds.tools.evet.Functions.contains;
import static net.joinedminds.tools.evet.Functions.isEmpty;

/**
 * Main System object.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Singleton
public class Evet {
    private static final Logger logger = LoggerFactory.getLogger(Evet.class);
    private ServletContext context;
    private Db db;
    public final AdjunctManager adjuncts;

    @Inject
    public Evet(ServletContext context, Db db) {
        this.context = context;
        this.db = db;
        adjuncts = new AdjunctManager(context, getClass().getClassLoader(), "adjuncts");
    }

    public AdjunctManager getAdjuncts() {
        return adjuncts;
    }

    public void doEndEvent(@QueryParameter(required = true) String id,
                           @QueryParameter(required = false) String title,
                           @QueryParameter(required = false) String description,
                           @QueryParameter(required = false) String[] tags,
                           StaplerRequest request, StaplerResponse response) throws IOException {
        logger.trace("doEndEvent({}, {}, {}, {})",id, title, description, Arrays.toString(tags));
        Map<String, String> extra = findExtraProperties(Db.RESERVED_NAMES, request);
        db.updateEventEnd(id, title, description, tags, extra);
        JSONObject json = new JSONObject();
        json.put("id", id);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        PrintWriter writer = response.getWriter();
        writer.println(json.toString());
        writer.flush();
    }

    public void doStartEvent(@QueryParameter(required = false) String id,
                             @QueryParameter(required = true) String system,
                             @QueryParameter(required = true) String title,
                             @QueryParameter(required = false) String node,
                             @QueryParameter(required = false) String description,
                             @QueryParameter(required = false) String[] tags,
                             StaplerRequest request, StaplerResponse response) throws IOException {
        logger.trace("doStartEvent({}, {}, {}, {}, {}, {})", id, system, title, node, description, Arrays.toString(tags));
        Map<String, String> extra = findExtraProperties(Db.RESERVED_NAMES, request);
        node = findNode(request, node);
        String retId = db.addBeginEvent(id, system, title, node, description, tags, extra);
        if (!Functions.isEmpty(retId)) {
            JSONObject json = new JSONObject();
            json.put("id", retId);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter writer = response.getWriter();
            writer.println(json.toString());
            writer.flush();
        } else {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            PrintWriter writer = response.getWriter();
            writer.println(new JSONObject(true).toString());
            writer.flush();
        }
    }

    public void doEvent(@QueryParameter(required = true) String system,
                        @QueryParameter(required = true) String title,
                        @QueryParameter(required = false) String node,
                        @QueryParameter(required = false) String description,
                        @QueryParameter(required = false) String[] tags,
                        StaplerRequest request, StaplerResponse response) throws IOException {
        logger.trace("doEvent({}, {}, {}, {}, {})", system, title, node, description, Arrays.toString(tags));
        Map<String, String> extra = findExtraProperties(Db.RESERVED_NAMES, request);
        node = findNode(request, node);
        String id = db.addEvent(system, title, node, description, tags, extra);
        if (!Functions.isEmpty(id)) {
            JSONObject json = new JSONObject();
            json.put("id", id);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter writer = response.getWriter();
            writer.println(json.toString());
            writer.flush();
        } else {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            PrintWriter writer = response.getWriter();
            writer.println(new JSONObject(true).toString());
            writer.flush();
        }
    }

    public void doHistoricalEvent(@QueryParameter(required = true) String system,
                                  @QueryParameter(required = true) String title,
                                  @QueryParameter(required = true) String start,
                                  @QueryParameter(required = false) String end,
                                  @QueryParameter(required = false) String node,
                                  @QueryParameter(required = false) String description,
                                  @QueryParameter(required = false) String[] tags,
                                  StaplerRequest request, StaplerResponse response) throws IOException {
        logger.trace("doHistoricalEvent({}, {}, {}, {}, {}, {}, {})", system, title, start, end, node, description,
                                                                Arrays.toString(tags));
        Map<String, String> extra = findExtraProperties(Db.RESERVED_NAMES, request);
        node = findNode(request, node);
        Date dStart = parseTime(start);
        Date dEnd = null;
        if(!isEmpty(end)) {
            dEnd = parseTime(end);
        }
        String id = db.addHistoricalEvent(system, dStart, dEnd, title, node, description, tags, extra);
        if (!Functions.isEmpty(id)) {
            JSONObject json = new JSONObject();
            json.put("id", id);
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_CREATED);
            PrintWriter writer = response.getWriter();
            writer.println(json.toString());
            writer.flush();
        } else {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            PrintWriter writer = response.getWriter();
            writer.println(new JSONObject(true).toString());
            writer.flush();
        }
    }

    private Date parseTime(String timestamp) {
        //First try to see if it is a long and assume it is the unix time.
        try {
            long ticks = Long.parseLong(timestamp);
            return new Date(ticks);
        } catch (NumberFormatException e) {
            //Nope, use whatever xsd:timestamp likes (2002-10-10T12:00:00-05:00)
            return DatatypeConverter.parseDateTime(timestamp).getTime();
        }
    }

    public ViewBuilder getTimeline() {
        return new TimelineViewBuilder(db);
    }

    public TimePlotViewBuilder getPlot() {
        return new TimePlotViewBuilder(db);
    }

    private String findNode(StaplerRequest request, String node) {
        if (!isEmpty(node)) {
            return node;
        } else {
            return request.getRemoteHost();
        }
    }

    private Map<String, String> findExtraProperties(List<String> exclude, StaplerRequest request) {
        Map<String, String> properties = new HashMap<>();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String)parameterNames.nextElement();
            if(!exclude.contains(name)) {
                properties.put(name, request.getParameter(name));
            }
        }
        return properties;
    }
}
