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


var SimileAjax = {
  Platform: {}
};


var tl;
function onLoad() {
    var eventSource1 = new Timeline.DefaultEventSource();
    var bandInfos = [
        Timeline.createBandInfo({
            width:          "80%",
            intervalUnit:   Timeline.DateTime.HOUR,
            eventSource:    eventSource1,
            intervalPixels: 100
        }),
        Timeline.createBandInfo({
            width:          "8%",
            intervalUnit:   Timeline.DateTime.DAY,
            eventSource:    eventSource1,
            overview:       true,
            intervalPixels: 100
        })
        ,
        Timeline.createBandInfo({
            width:          "6%",
            intervalUnit:   Timeline.DateTime.WEEK,
            eventSource:    eventSource1,
            overview:       true,
            intervalPixels: 70
        })
        ,
        Timeline.createBandInfo({
            width:          "6%",
            intervalUnit:   Timeline.DateTime.MONTH,
            eventSource:    eventSource1,
            overview:       true,
            intervalPixels: 50
        })
    ];
    bandInfos[1].syncWith = 0;
    bandInfos[1].highlight = true;
    bandInfos[2].syncWith = 1;
    bandInfos[2].highlight = true;
    bandInfos[3].syncWith = 2;
    bandInfos[3].highlight = true;
    tl = Timeline.create(document.getElementById("the-timeline"), bandInfos);

    tl.loadJSON("json", function(json, url) {
        eventSource1.loadJSON(json, url);
    });
}

var resizeTimerID = null;
function onResize() {
    if (resizeTimerID == null) {
        resizeTimerID = window.setTimeout(function() {
            resizeTimerID = null;
            tl.layout();
        }, 500);
    }
}