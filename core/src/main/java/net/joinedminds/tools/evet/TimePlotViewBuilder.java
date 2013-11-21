package net.joinedminds.tools.evet;

/**
 * Number of events per day
 */
public class TimePlotViewBuilder extends ViewBuilder {

    private Db.CountResolution resolution = Db.CountResolution.Day;

    public TimePlotViewBuilder(Db db) {
        super(db);
    }

    public TimeValueTable getData() {
        ensureStart();
        ensureEnd();
        return db.countEvents(resolution, start, end, systems, tags, nodes);
    }

    public TimeValueTable doCsv() {
        TimeValueTable data = getData();
        data.setFormat(TimeValueTable.Format.Csv);
        return data;
    }

    public TimeValueTable doJson() {
        TimeValueTable data = getData();
        data.setFormat(TimeValueTable.Format.Json);
        return data;
    }

    public TimeValueTable doJsonp() {
        TimeValueTable data = getData();
        data.setFormat(TimeValueTable.Format.Jsonp);
        return data;
    }

    public TimePlotViewBuilder getResolution(String res) {
        resolution = Db.CountResolution.valueOf(res);
        return this;
    }
}
