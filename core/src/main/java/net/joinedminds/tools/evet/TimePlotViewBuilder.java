package net.joinedminds.tools.evet;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Number of events per day
 */
public class TimePlotViewBuilder extends ViewBuilder {

    public TimePlotViewBuilder(Db db) {
        super(db);
    }

    public List<Map.Entry<Date,Integer>> getData() {
        ensureStart();
        ensureEnd();
        return db.countEvents(start, end, systems, tags, nodes);
    }
}
