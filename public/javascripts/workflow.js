function updateWorkflowRuns(data) {
        if (data.length != 0) {
            data.sort(function (a, b) {
                return new Date(b.startTime).getTime() - new Date(a.startTime).getTime();
            });

            var html = "    <table>" +
                "        <tr>" +
                "            <th>Workflow</th>" +
                "            <th>Start Time</th>" +
                "            <th>End Time</th>" +
                "            <th>Result</th>" +
                "            <th>Output Log</th>" +
                "            <th>Error Log</th>" +
                "        </tr>";

            $.each(data, function (i, val) {
                html += "        <tr>" +
                    "            <td>" + val.workflow + "</td>" +
                    "            <td>" + val.startTime + "</td>" +
                    "            <td>" + (val.endTime != null ? val.endTime : "Running...") + "</td>" +
                    "            <td>" + (val.hasResult ? "<a href=\"result/" + val.id + "\">Download</a>" : "Unavailable") + "</td>" +
                    "            <td>" + (val.hasOutput ? "<a href=\"output/" + val.id + "\">Output log</a>" : "No Output") + "</td>" +
                    "            <td>" + (val.hasErrors ? "<a href=\"error/" + val.id + "\">Error log</a>" : "No Errors") + "</td>" +
                    "        </tr>";
            });

            html += "    </table>";
            $("#workflowRuns").html(html);
        }
        console.log(data);
    }