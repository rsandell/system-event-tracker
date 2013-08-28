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

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import org.junit.Before;

import java.io.IOException;

/**
 * Abstract class to be extended for running tests with Embedded MongoDB.
 * A knowledgeBase is created during setUp, which is backed up by a
 * real MongoDB instance.
 *
 * @author Fredrik Persson &lt;fredrik6.persson@sonymobile.com&gt;
 *
 */
public abstract class EmbeddedMongoTest {

    public static final String LOCALHOST = "127.0.0.1";
    public static final String DB_NAME = "evettest";

    private static String mongoURL = System.getProperty(EmbeddedMongoTest.class.getName() + ".mongoURL");

    protected int port;

    /**
     * Sets up an instance of a real MongoDB, to be used for testing.
     * @throws IOException if something goes wrong
     */
    @Before
    public void setUpMongo() throws IOException {
        MongodStarter runtime;
        if (mongoURL != null) {
            // Use separate URL for fetching mongoDB artifacts.
            Command command = Command.MongoD;
            de.flapdoodle.embed.process.config.store.DownloadConfigBuilder downloadConf = new DownloadConfigBuilder()
                    .defaultsForCommand(command).downloadPath(mongoURL);
            de.flapdoodle.embed.process.store.ArtifactStoreBuilder artifactStoreBuilder = new ArtifactStoreBuilder()
                    .defaults(command).download(downloadConf);

            IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                    .defaults(command)
                    .artifactStore(artifactStoreBuilder)
                    .build();

            runtime = MongodStarter.getInstance(runtimeConfig);
        } else {
            runtime = MongodStarter.getDefaultInstance();
        }

        IMongodConfig conf = new MongodConfigBuilder().version(Version.Main.V2_4).build();
        MongodExecutable mongodExe = runtime.prepare(conf);
        mongodExe.start();

        port = conf.net().getPort();
    }
}
