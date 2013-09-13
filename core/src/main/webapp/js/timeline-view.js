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

function formStart(list) {
    var start = $("#filterForm input[name='start']").val();
    if (start != null && start.length > 0) {
        list.push("start");
        list.push(start);
    }
    start = $("#filterForm input[name='startHours']").val();
    if (start != null && start != 0) {
        list.push("startHours");
        list.push(start);
    }
    start = $("#filterForm input[name='startDays']").val();
    if (start != null && start != 0) {
        list.push("startDays");
        list.push(start);
    }
    start = $("#filterForm input[name='startWeeks']").val();
    if (start != null && start != 0) {
        list.push("startWeeks");
        list.push(start);
    }
    start = $("#filterForm input[name='startMonths']").val();
    if (start != null && start != 0) {
        list.push("startMonths");
        list.push(start);
    }
    start = $("#filterForm input[name='startYears']").val();
    if (start != null && start != 0) {
        list.push("startYears");
        list.push(start);
    }
}

function formEnd(list) {
    var end = $("#filterForm input[name='end']").val();
    if (end != null && end.length > 0) {
        list.push("end");
        list.push(end);
    }
    end = $("#filterForm input[name='endHours']").val();
    if (end != null && end != 0) {
        list.push("endHours");
        list.push(end);
    }
    end = $("#filterForm input[name='endDays']").val();
    if (end != null && end != 0) {
        list.push("endDays");
        list.push(end);
    }
    end = $("#filterForm input[name='endWeeks']").val();
    if (end != null && end != 0) {
        list.push("endWeeks");
        list.push(end);
    }
    end = $("#filterForm input[name='endMonths']").val();
    if (end != null && end != 0) {
        list.push("endMonths");
        list.push(end);
    }
    end = $("#filterForm input[name='endYears']").val();
    if (end != null && end != 0) {
        list.push("endYears");
        list.push(end);
    }
}

function formData(list) {
    var data = $("#filterForm input[name='systems']").val();
    if (data != null && data.length > 0) {
        list.push("systems");
        list.push(data);
    }
    data = $("#filterForm input[name='tags']").val();
    if (data != null && data.length > 0) {
        list.push("tags");
        list.push(data);
    }
    data = $("#filterForm input[name='nodes']").val();
    if (data != null && data.length > 0) {
        list.push("nodes");
        list.push(data);
    }
}

function filterFormSubmit() {
    var params = [];
    if (rootUrl != "") {
        params.push(rootUrl);
    }
    params.push("timeline");
    formStart(params);
    formEnd(params);
    formData(params);
    var url = params.join("/");
    window.location = "/" + url;
}