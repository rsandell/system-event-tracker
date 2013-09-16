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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.koshuke.stapler.simile.timeline.TimelineEventList;
import org.mockito.internal.util.collections.Sets;

import javax.servlet.ServletContext;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link Db}.
 *
 * @author Robert Sandell
 */
public class DbTest extends EmbeddedMongoTest {

    private Injector injector;
    private Db db;

    @Before
    public void setUp() {
        ServletContext context = mock(ServletContext.class);
        injector = Guice.createInjector(new GuiceModule(context, LOCALHOST, port, DB_NAME, "", ""));
        db = injector.getInstance(Db.class);
    }

    @Test
    public void testAddBeginEvent() throws Exception {
        String myId = UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        Map<String, String> extra = new HashMap<>();
        extra.put("help", "me");
        String id = db.addBeginEvent(myId, "JUnit", "testAddBeginEvent", "localhost",
                null, new String[]{"test", "bosse"}, extra);
        assertEquals(myId, id);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        TimelineEventList events = db.findEvents(c, Calendar.getInstance(), null, null, null);
        assertEquals(1, events.size());
        DbEvent event = (DbEvent) events.get(0);
        assertEquals(id, event.id);
        assertNull(event.end);
        assertNotNull(event.start);
        assertTrue(event.durationEvent);
        assertEquals(2, event.tags.length);
        assertNotNull(event.get("help"));
        assertEquals("me", event.get("help"));
    }

    @Test
    public void testUpdateEventEnd() throws Exception {
        String id = db.addBeginEvent(null, "JUnit", "testAddBeginEvent", "localhost",
                null, new String[]{"test", "bosse"}, null);
        Thread.sleep(2000);
        db.updateEventEnd(id, "JJ", null, new String[]{"test", "bobby"}, null);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        TimelineEventList events = db.findEvents(c, Calendar.getInstance(), null, null, null);
        assertEquals(1, events.size());
        DbEvent event = (DbEvent) events.get(0);
        assertEquals(id, event.id);
        assertNotNull(event.end);
        assertNotNull(event.start);
        assertTrue(event.durationEvent);
        assertEquals(3, event.tags.length);
    }

    @Test
    public void testAddEvent() throws Exception {
        Map<String, String> extra = new HashMap<>();
        extra.put("help", "me");
        String id = db.addEvent("JUnit", "testAddEvent", "localhost", null, new String[]{"test", "mongo"}, extra);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        TimelineEventList events = db.findEvents(c, Calendar.getInstance(), null, null, null);
        assertEquals(1, events.size());
        DbEvent event = (DbEvent) events.get(0);
        assertEquals(id, event.id);
        assertNull(event.end);
        assertNotNull(event.start);
        assertFalse(event.durationEvent);
        assertEquals(2, event.tags.length);
        assertNotNull(event.get("help"));
        assertEquals("me", event.get("help"));
    }

    @Test
    public void testAddHistoricalEvent() throws Exception {
        Map<String, String> extra = new HashMap<>();
        extra.put("help", "me");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, -1);
        String id = db.addHistoricalEvent("JUnit", c.getTime(), null,
                "testAddEvent", "localhost", null, new String[]{"test", "mongo"}, extra);

        c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        TimelineEventList events = db.findEvents(c, Calendar.getInstance(), null, null, null);
        assertEquals(1, events.size());
        DbEvent event = (DbEvent) events.get(0);
        assertEquals(id, event.id);
        assertNull(event.end);
        assertNotNull(event.start);
        assertFalse(event.durationEvent);
        assertEquals(2, event.tags.length);
        assertNotNull(event.get("help"));
        assertEquals("me", event.get("help"));
    }

    @Test
    public void testAddHistoricalEventBefore() throws Exception {
        Map<String, String> extra = new HashMap<>();
        extra.put("help", "me");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -2);
        String id = db.addHistoricalEvent("JUnit", c.getTime(), null,
                "testAddEvent", "localhost", null, new String[]{"test", "mongo"}, extra);

        c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        TimelineEventList events = db.findEvents(c, Calendar.getInstance(), null, null, null);
        assertEquals(0, events.size());
    }

    @Test
    public void testCountEvents() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        db.addHistoricalEvent("Test", calendar.getTime(), null, "Testit", "node", "", null, null);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        db.addHistoricalEvent("Test", calendar.getTime(), null, "Testit", "node", "", null, null);
        calendar.set(Calendar.DAY_OF_MONTH, 3);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        db.addHistoricalEvent("Test", calendar.getTime(), null, "Testit", "node", "", null, null);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        db.addHistoricalEvent("Test", calendar.getTime(), null, "Testit", "node", "", null, null);


        calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        Calendar end = (Calendar)calendar.clone();
        end.set(Calendar.DAY_OF_MONTH, 4);
        TimeValueTable table = db.countEvents(calendar, end, Sets.newSet("Test"), null, null);
        assertEquals(2, table.keySet().size());
        assertEquals(2, (int)table.get(table.keySet().iterator().next()).get(0));
    }
}
