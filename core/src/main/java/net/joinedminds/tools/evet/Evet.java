package net.joinedminds.tools.evet;

import com.google.inject.Singleton;
import com.google.inject.Inject;
import org.kohsuke.stapler.framework.adjunct.AdjunctManager;

import javax.servlet.ServletContext;

/**
 * Main System object.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
@Singleton
public class Evet {

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
}
