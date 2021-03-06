/*
 * The MIT License
 *
 * Copyright (c) 2012-, Robert Sandell-sandell.robert@gmail.com. All rights reserved.
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
import org.kohsuke.stapler.Stapler;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.joinedminds.tools.evet.Functions.isEmpty;

/**
 * Main initializer for the Web App.
 *
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class WebAppMain implements ServletContextListener {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(WebAppMain.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String dbHost = checkNotNull(Settings.DbHost.findValue(context), "Missing database host setting.");
        String dbName = checkNotNull(Settings.DbName.findValue(context), "Missing database name setting.");
        String dbPortS = Settings.DbPort.findValue(context);
        Integer dbPort = null;
        if(!isEmpty(dbPortS)) {
            try {
                dbPort = Integer.parseInt(dbPortS);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Bad port number " + dbPortS, e);
            }
        }
        String dbUser = Settings.DbUser.findValue(context);
        String dbPasswd = Settings.DbPassword.findValue(context);
        logger.info("Starting up with db host: {}, port: {}, name: {} user: {}", dbHost, dbPort, dbName, dbUser);
        Injector injector = Guice.createInjector(new GuiceModule(context, dbHost, dbPort, dbName, dbUser, dbPasswd));
        Stapler.setRoot(sce, injector.getInstance(Evet.class));
        logger.info("Started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    static enum Settings {
        DbHost("evet.DbHost", "EVET_DB_HOST", "DB_HOST", null),
        DbName("evet.DbName", "EVET_DB_NAME", "DB_NAME", null),
        DbPort("evet.DbPort", "EVET_DB_PORT", "DB_PORT", null),
        DbUser("evet.DbUser", "EVET_DB_USER", "DB_USER", ""),
        DbPassword("evet.DbPassword", "EVET_DB_PASSWORD", "DB_PASSWD", "");

        final String propertiesName;
        final String environmentName;
        final String contextInitParameter;
        final String defaultValue;

        Settings(String propertiesName, String environmentName, String contextInitParameter, String defaultValue) {
            this.propertiesName = propertiesName;
            this.environmentName = environmentName;
            this.contextInitParameter = contextInitParameter;
            this.defaultValue = defaultValue;
        }

        public String findValue(ServletContext context) {
            if(!isEmpty(System.getProperty(propertiesName))) {
                return System.getProperty(propertiesName);
            } else if(!isEmpty(System.getenv(environmentName))) {
                return System.getenv(environmentName);
            } else {
                return Functions.ifNull(context.getInitParameter(contextInitParameter), defaultValue);
            }
        }
    }
}
