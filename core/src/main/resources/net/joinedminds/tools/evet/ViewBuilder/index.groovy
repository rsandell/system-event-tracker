import net.joinedminds.tools.evet.Functions

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

def st = namespace("jelly:stapler")

String rootUrl = Functions.rootUrl

st.header(name: "Expires", value: "0")
st.header(name: "Cache-Control", value: "no-cache,must-revalidate")
st.contentType(value: "text/html;charset=UTF-8")

html {
    head {
        title("Event Timeline")
        st.adjunct(includes: "org.kohsuke.stapler.jquery")
        st.adjunct(includes: "org.kohsuke.stapler.bootstrap-responsive")
        st.adjunct(includes: "org.kohsuke.stapler.fontawesome")
        script(src: "${rootUrl}/js/timeline-view.js", type: "text/javascript")
        st.adjunct(includes: "org.kohsuke.stapler.simile.timeline")
    }
    body(onload: "onLoad();", onresize: "onResize();") {
        div(id: "the-timeline", style: "height: 450px; width: 100%; margin: 3px; border: 1px solid #aaa")
    }
}