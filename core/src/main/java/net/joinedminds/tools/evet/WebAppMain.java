package net.joinedminds.tools.evet;/*
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

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.kohsuke.stapler.Stapler;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Robert Sandell &lt;sandell.robert@gmail.com&gt;
 */
public class WebAppMain implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String dbHost = checkNotNull(context.getInitParameter("DB_HOST"), "Missing DB_HOST parameter.");
        String dbName = checkNotNull(context.getInitParameter("DB_NAME"), "Missing DB_NAME parameter.");
        String dbUser = Functions.ifNull(context.getInitParameter("DB_USER"), "");
        String dbPasswd = Functions.ifNull(context.getInitParameter("DB_PASSWD"), "");

        Injector injector = Guice.createInjector(new GuiceModule(context, dbHost, dbName, dbUser, dbPasswd));
        Stapler.setRoot(sce, injector.getInstance(Evet.class));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
