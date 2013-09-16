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

var timeplot;

function onLoad() {
    var eventSource = new Timeplot.DefaultEventSource();
    var plotInfo = [
        Timeplot.createPlotInfo({
            id: "plot1",
            dataSource: new Timeplot.ColumnSource(eventSource,1),
            valueGeometry: new Timeplot.DefaultValueGeometry({
                gridColor: "#000000",
                axisLabelsPlacement: "left",
                min: 0
            }),
            timeGeometry: new Timeplot.DefaultTimeGeometry({
                gridColor: "#000000",
                axisLabelsPlacement: "top"
            }),
            lineColor: "#ff0000",
            fillColor: "#cc8080",
            showValues: true
        })
    ];

    timeplot = Timeplot.create(document.getElementById("the-timeplot"), plotInfo);
    timeplot.loadText("csv", ",", eventSource);
}

var resizeTimerID = null;
function onResize() {
    if (resizeTimerID == null) {
        resizeTimerID = window.setTimeout(function() {
            resizeTimerID = null;
            timeplot.repaint();
        }, 100);
    }
}


function filterFormSubmit() {
    var params = [];
    if (rootUrl != "") {
        params.push(rootUrl);
    }
    params.push("plot");
    formStart(params);
    formEnd(params);
    formData(params);
    var url = params.join("/");
    window.location = "/" + url;
}