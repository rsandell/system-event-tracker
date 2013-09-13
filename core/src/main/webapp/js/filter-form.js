
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