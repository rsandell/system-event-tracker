package net.joinedminds.tools.evet;

import org.kohsuke.stapler.StaplerResponse;
import org.koshuke.stapler.simile.timeline.TimelineEventList;

import java.io.IOException;

/**
 * The timeline view.
 */
public class TimelineViewBuilder extends ViewBuilder {

    public TimelineViewBuilder(Db db) {
        super(db);
    }

    public TimelineEventList doJson(StaplerResponse response) throws IOException {
        ensureStart();
        ensureEnd();
        return db.findEvents(start, end, systems, tags, nodes);
    }
}
