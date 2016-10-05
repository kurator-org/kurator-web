function updateWorkflowRuns(data) {
        if (data.length != 0) {
            data.sort(function (a, b) {
                var a = new Date(a.startTime).getTime();
                var b = new Date(b.startTime).getTime();
                return a>b ? -1 : a<b ? 1 : 0;
            });

            var html = "    <table class=\"table\">" +
                "        <tr>" +
                "            <th>Workflow</th>" +
                "            <th>Start Time</th>" +
                "            <th>End Time</th>" +
                "            <th>Result</th>" +
                "            <th>Output Log</th>" +
                "            <th>Error Log</th>" +
                "            <th>Status</th>" +
                "        </tr>";

            $.each(data, function (i, val) {
                html += "        <tr>" +
                    "            <td>" + val.workflow + "</td>" +
                    "            <td>" + val.startTime + "</td>";

                if (val.status == "RUNNING") {
                    html +=  "<td class=\"loading\"></td>" +
                            "<td class=\"loading\"></td>" +
                            "<td class=\"loading\"></td>" +
                            "<td class=\"loading\"></td>" +
                            "<td><span class=\"label label-primary\">Running</span></td>"

                } else if (val.status == "SUCCESS") {
                    html += "<td>" + val.endTime + "</td>" +
                        "            <td>" + (val.hasResult ? "<a href=\"result/" + val.id + "\">Download</a>" : "Unavailable") + "</td>" +
                        "            <td>" + (val.hasOutput ? "<a href=\"output/" + val.id + "\">Output log</a>" : "No Output") + "</td>" +
                        "            <td>" + (val.hasErrors ? "<a href=\"error/" + val.id + "\">Error log</a>" : "No Errors") + "</td>" +
                        "<td><span class=\"label label-success\">Complete</span></td>"
                } else if (val.status == "ERROR") {
                    html += "<td>" + val.endTime + "</td>" +
                        "            <td>" + (val.hasResult ? "<a href=\"result/" + val.id + "\">Download</a>" : "Unavailable") + "</td>" +
                        "            <td>" + (val.hasOutput ? "<a href=\"output/" + val.id + "\">Output log</a>" : "No Output") + "</td>" +
                        "            <td>" + (val.hasErrors ? "<a href=\"error/" + val.id + "\">Error log</a>" : "No Errors") + "</td>" +
                        "<td><span class=\"label label-danger\">Error</span></td>"
                } else {
                    html += "            <td>" + (val.endTime != null ? val.endTime : "...") + "</td>" +
                        "            <td>" + (val.hasResult ? "<a href=\"result/" + val.id + "\">Download</a>" : "Unavailable") + "</td>" +
                        "            <td>" + (val.hasOutput ? "<a href=\"output/" + val.id + "\">Output log</a>" : "No Output") + "</td>" +
                        "            <td>" + (val.hasErrors ? "<a href=\"error/" + val.id + "\">Error log</a>" : "No Errors") + "</td>" +
                        "<td>" + val.status + "</td>";

                }

                html += "</tr>";
            });

            html += "    </table>";
            $("#workflow-runs").html(html);
        } else {
            $("#workflow-runs").html("<i>No workflow runs</i>");
        }
        console.log(data);
    }