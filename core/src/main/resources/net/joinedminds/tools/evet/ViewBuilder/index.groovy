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

import net.joinedminds.tools.evet.Functions
import net.joinedminds.tools.evet.ViewBuilder

def st = namespace("jelly:stapler")

ViewBuilder view = my

String rootUrl = Functions.rootUrl

st.header(name: "Expires", value: "0")
st.header(name: "Cache-Control", value: "no-cache,must-revalidate")
st.contentType(value: "text/html;charset=UTF-8")

html(height: "100%") {
    head {
        title("Event Timeline")
        st.adjunct(includes: "org.kohsuke.stapler.jquery")
        st.adjunct(includes: "org.kohsuke.stapler.bootstrap-responsive")
        st.adjunct(includes: "org.kohsuke.stapler.fontawesome")
        script(type: "text/javascript") {
            raw("var rootUrl = '${rootUrl}';")
        }
        script(src: "${rootUrl}/js/filter-form.js", type: "text/javascript")
        st.include(it: my, page: "head.groovy")
        link(rel: "stylesheet", href: "${rootUrl}/style.css")
    }
    body(onload: "onLoad();", onresize: "onResize();", height: "100%") {
        div(class: "container-fluid") {
            div(class: "row-fluid", style: "height: 100%;") {
                div(class: "span2", style: "height: 100%", id: "menu") {
                    div(class: "row-fluid", id: "calculated") {
                        div (class: "span2") {
                            strong("From: ")
                            span(view.startString())
                            br()
                            strong("To: ")
                            span(view.endString())
                        }
                    }
                    form(id: "filterForm") {
                        fieldset {
                            legend("Start")
                            label("Timestamp")
                            input(type: "text", name: "start", value: view.formData.startString())
                            label("Hours")
                            input(type: "number", name: "startHours", value: view.formData.startHours)
                            label("Days")
                            input(type: "number", name: "startDays", value: view.formData.startDays)
                            label("Weeks")
                            input(type: "number", name: "startWeeks", value: view.formData.startWeeks)
                            label("Months")
                            input(type: "number", name: "startMonths", value: view.formData.startMonths)
                            label("Years")
                            input(type: "number", name: "startYears", value: view.formData.startYears)
                            legend("End")
                            label("Timestamp")
                            input(type: "text", name: "end", value: view.formData.endString())
                            label("Hours")
                            input(type: "number", name: "endHours", value: view.formData.endHours)
                            label("Days")
                            input(type: "number", name: "endDays", value: view.formData.endDays)
                            label("Weeks")
                            input(type: "number", name: "endWeeks", value: view.formData.endWeeks)
                            label("Months")
                            input(type: "number", name: "endMonths", value: view.formData.endMonths)
                            label("Years")
                            input(type: "number", name: "endYears", value: view.formData.endYears)
                            legend("Data")
                            label("Systems")
                            input(type: "text", name: "systems", value: view.systems?.join(","))
                            label("Tags")
                            input(type: "text", name: "tags", value: view.tags?.join(","))
                            label("Nodes")
                            input(type: "text", name: "nodes", value: view.nodes?.join(","))
                            button(type: "button", class: "btn", onclick:"filterFormSubmit();", "Filter")
                        }
                    }
                }
                div(class: "span10", style: "height: 100%") {
                    st.include(it: my, page: "main.groovy")
                }
            }
        }
    }
}
