// tail effect
function tailScroll() {
    var height = $("#output").get(0).scrollHeight;
    $("#output").scrollTop(height);
}
var base = document.URL;
var context = base.substr(0, base.indexOf("/", base.indexOf("/", base.indexOf("//") + 2) + 1));

context = context.substring(context.indexOf("//")+2); // remove http scheme

var wsUri = "ws://" + context + "/builder/socket";
var output;

function initWebSocket() {
    output = document.getElementById("output");
    testWebSocket();
}

function testWebSocket() {
    websocket = new WebSocket(wsUri);
    websocket.onopen = function (evt) {
        onOpen(evt)
    };
    websocket.onclose = function (evt) {
        onClose(evt)
    };
    websocket.onmessage = function (evt) {
        onMessage(evt)
    };
    websocket.onerror = function (evt) {
        onError(evt)
    };
}

function onOpen(evt) {
    writeToScreen("CONNECTED");
}

function onClose(evt) {
    writeToScreen("DISCONNECTED");
}

function onMessage(evt) {
    var data = evt.data;

    if (isJsonString(data)) {
        var json = $.parseJSON(data);

        var text = "";

        json.forEach(function(entry) {
            text += entry.word + ": " + entry.count + "<br />";
        });

        writeToScreen('<span style="color: blue;">' + text + '</span>');
        updateHistogram(json);
    } else {
        writeToScreen('<span style="color: blue;">' + data + '</span>');
    }
}

function onError(evt) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

function doSend(message) {
    writeToScreen("SENT: " + message);
    websocket.send(message);
}

function writeToScreen(message) {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    output.appendChild(pre);

    tailScroll();
}

function isJsonString(str) {
    try {
        $.parseJSON(str);
    } catch (e) {
        return false;
    }
    return true;
}

var x = d3.scale.linear()
    .domain([0, 200])
    .range([0, 420]);

function updateHistogram(data) {
    $(".chart").empty();

    d3.select(".chart")
        .selectAll("div")
        .data(data, function(d) { return d.count; })
        .enter().append("div")
        .style("width", function(d) { return x(d.count) + "px"; })
        .text(function(d) { return d.word; });
}