function RunResult() {
    this._elem = $('<tr>');
}

RunResult.prototype = {
    getElem : function () {
        return this._elem
    },

    addText : function (innerHtml) {
        this._elem.append($('<td>'+innerHtml+'</td>'));
    },

    addProduct: function (baseUrl, runId) {
        this._elem.append($('<td>'))
            .append($('<a>', {href : baseUrl+'/'+ runId}))
            .append($('Result'))
            .append($('</a></td>'));

    },

    addStatus: function (status) {
        var labelClass, text;

        if (status == "RUNNING") {
            labelClass = 'label-primary';
            text = 'Running...';
        } else if (status == "SUCCESS") {
            labelClass = 'label-success';
            text = 'Complete';
        } else if (status == "ERRORS") {
            labelClass = 'label-danger';
            text = 'Errors';
        }

        this._elem.append($('<td><span class="label">', {class : labelClass}))
            .append($(text+'</span></td>'));
    }
}

function WorkflowRuns(runs) {
    this._elem = $('<table>', {class : 'table'});

    runs.forEach(function (run) {

    })
}


function buildRow(run) {
    var row = $('<tr>')

    row.append();
    row.append($('<td>'+run.startTime+'</td>'));

    if (run.endTime) {
        row.append($('<td>'+run.endTime+'</td>'));
    } else {
        row.append($('<td>Running...</td>'));
    }

    if (run.hasResult) {
        var url =
        row.append();
    } else {
        row.append($('<td>Unavailable</td>'));
    }

    if (run.hasOutput) {
        row.append($('<td><a href="'+run.output+'">Output Log</a></td>'));
    } else {
        row.append($('<td>No Output</td>'));
    }

    if (run.hasErrors) {
        row.append($('<td><a href="'+run.errors+'">Error Log</a></td>'));
    } else {
        row.append($('<td>No Errors</td>'));
    }

    if (run.status == "RUNNING") {
        row.append($('<td><span class="label label-primary">Running</span></td>'));
    } else if (run.status == "SUCCESS") {
        row.append($('<td><span class="label label-success">Complete</span></td>'));
    } else if (run.status == "ERRORS") {
        row.append($('<td><span class="label label-danger">Errors</span></td>'));
    }

    row.append($('</tr>'));
}

function buildTable(runs) {
    var table = $('#workflow-runs');
    table.apppend($('<table>', {class: 'table'}));
    
    runs.forEach(function (run) {
        table.append(buildRow(run));
    })

    table.append($('</table>'));
}

var workflowRuns = [{
    "runId": 1,
    "workflow": "Darwin Core Archive Controlled Field Assessor",
    "startTime": "2016-10-24T14:58:33",
    "status": "RUNNING",
    "hasResult": false,
    "hasOutput": false,
    "hasErrors": false
}, {
    "runId": 2,
    "workflow": "Darwin Core Archive Controlled Field Assessor",
    "startTime": "2016-10-24T14:58:33", "endTime": "2016-10-24T14:58:57",
    "status": "SUCCESS",
    "hasResult": true,
    "hasOutput": true,
    "hasErrors": false
}, {
    "runId": 3,
    "workflow": "Darwin Core Archive Controlled Field Assessor",
    "startTime": "2016-10-24T14:58:33", "endTime": "2016-10-24T14:58:57",
    "status": "SUCCESS",
    "hasResult": true,
    "hasOutput": true,
    "hasErrors": true
}, {
    "runId": 4,
    "workflow": "Darwin Core Archive Controlled Field Assessor",
    "startTime": "2016-10-24T14:58:33",
    "status": "ERROR",
    "hasResult": false,
    "hasOutput": true,
    "hasErrors": true
}, {
    "runId": 5,
    "workflow": "Darwin Core Archive Controlled Field Assessor",
    "startTime": "2016-10-24T14:58:33", "endTime": "2016-10-24T14:58:57",
    "status": "SUCCESS",
    "hasResult": true,
    "hasOutput": true,
    "hasErrors": true
}, {
    "runId": 6,
    "workflow": "Darwin Core Archive Controlled Field Assessor",
    "startTime": "2016-10-24T14:58:33", "endTime": "2016-10-24T14:58:57",
    "status": "SUCCESS",
    "hasResult": true,
    "hasOutput": true,
    "hasErrors": true
}, {
    "runId": 7,
    "workflow": "Darwin Core Archive Controlled Field Assessor",
    "startTime": "2016-10-24T14:58:33", "endTime": "2016-10-24T14:58:57",
    "status": "SUCCESS",
    "hasResult": true,
    "hasOutput": true,
    "hasErrors": true
}];