package net.joinedminds.tools.evet.client;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.org.mozilla.javascript.internal.json.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Description
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class FakeClient {

    public static final String BASE_ENDPOINT = "http://localhost:8080/";

    public static enum Types {
        ONE_SHOT("event"), DURATION_START("startEvent"), DURATION_END("endEvent"), NONE("");

        String endPoint;

        private Types(String endPoint) {
            this.endPoint = BASE_ENDPOINT + endPoint;
        }

        public String getEndPoint() {
            return endPoint;
        }
    }

    public static final Types[] EVENT_DISTRIBUTION = {
            Types.NONE, Types.NONE,
            Types.ONE_SHOT, Types.ONE_SHOT, Types.ONE_SHOT,
            Types.DURATION_START, Types.DURATION_END,
    };

    public static final String[] SYSTEMS = {"Jenkins", "Evet", "Users", "IDE", "Database"};
    public static final String[] TAGS = {"Build", "Login", "Logout", "Error", "Down", "Up"};
    public static final String[] NODES = {"one", "two", "three", "four", "five", "six", "seven"};

    public static void main(String[] args) throws IOException {
        Timer timer = new Timer("Event Generator", true);
        final Random random = new Random();
        final Queue<String> idQueue = new LinkedList<>();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Running...");
                try {
                    switch (randObject(random, EVENT_DISTRIBUTION)) {
                        case ONE_SHOT:
                            event(Types.ONE_SHOT, random);
                            break;
                        case DURATION_START:
                            String id = event(Types.DURATION_START, random);
                            idQueue.offer(id);
                            break;
                        case DURATION_END:
                            String lastId = idQueue.poll();
                            if (lastId != null) {
                                endEvent(lastId);
                            }
                            break;
                        case NONE:
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000, 60000);

        System.out.println("Press Return to end");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }

    private static void endEvent(String lastId) throws URISyntaxException, IOException {
        URI uri = new URIBuilder(Types.DURATION_END.endPoint).addParameter("id", lastId).build();
        System.out.println(uri.toString());
        execute(uri);
    }

    private static String event(Types type, Random random) throws URISyntaxException, IOException {
        String system = randObject(random, SYSTEMS);
        String[] tags = generateTags(random);
        String node = randObject(random, NODES);
        String title = system + "-" + randObject(random, TAGS);

        URI uri = new URIBuilder(type.endPoint).
                addParameter("system", system).
                addParameter("title", title).
                addParameter("node", node).
                addParameter("tags", Joiner.on(",").join(tags)).build();
        System.out.println(uri.toString());
        return execute(uri);
    }

    private static String execute(URI uri) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        String res = null;
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            res = getId(entity1);
        } finally {
            response1.close();
        }
        return res;
    }

    private static String getId(HttpEntity entity) throws IOException {
        String s = EntityUtils.toString(entity);
        JSONObject json = JSONObject.fromObject(s.trim());
        if (json == null || json.isNullObject()) {
            return null;
        } else {
            return (String)json.get("id");
        }
    }

    private static String[] generateTags(Random random) {
        Set<String> s = Sets.newHashSet();
        int nr = random.nextInt(TAGS.length - 1) + 1;
        for (int i = 0; i < nr; i++) {
            s.add(randObject(random, TAGS));
        }
        return s.toArray(new String[s.size()]);
    }

    private static <T> T randObject(Random random, T[] objs) {
        return objs[random.nextInt(objs.length)];
    }
}
