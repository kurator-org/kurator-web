var hitOptions = {
    segments: true,
    stroke: true,
    fill: true,
    tolerance: 5
};

function createRectangle(position, actor) {
    actors.push(actor);

    var shape = new Shape.Rectangle();
    shape.strokeColor = 'black';
    shape.fillColor = '#167ee5';

    var boundingRectangle = new Rectangle(position);
    shape.position = position;

    var outAnchorGroup = new Group([]);
    var inAnchorGroup = new Group([]);
    var shapeGroup = new Group([shape, outAnchorGroup, inAnchorGroup]);

    actor.boundingRectangle = boundingRectangle;
    actor.shape = shape;
    actor.shapeGroup = shapeGroup;
    actor.outAnchorGroup = outAnchorGroup;
    actor.inAnchorGroup = inAnchorGroup;

    shapeGroup.data.actor = actor;
    shapeGroup.data.type = 'actor';

    addTitle(actor, actor.name);

    actor.inputs.forEach(function(param) {
        addInput(actor, param);
    });

    actor.outputs.forEach(function(param) {
        addOutput(actor, param);
    });

}

function addTitle(actor, name) {
    var boundingRectangle = actor.boundingRectangle;

    var titleRectangle = new Shape.Rectangle();
    titleRectangle.strokeColor = 'black';
    titleRectangle.fillColor = 'white';

    var titleText = new PointText();
    titleText.justification = 'center';
    titleText.fillColor = 'black';
    titleText.content = name;

    var titlePosition = boundingRectangle.topCenter + new Point(titleText.bounds.width/2, -10);
    console.log(titlePosition);

    var padding = 2;
    titleRectangle.size = new Size(titleText.bounds.width, titleText.bounds.height);
    boundingRectangle.right = boundingRectangle.left + titleText.bounds.width;

    actor.shapeGroup.addChild(titleRectangle);
    actor.shapeGroup.addChild(titleText);

    titleText.position = titlePosition;
    titleRectangle.position = titleText.bounds.center;
    console.log(titleText.bounds.center);
}

function addInput(actor, input) {
    var boundingRectangle = actor.boundingRectangle;
    var shape = actor.shape;
    var shapeGroup = actor.shapeGroup;
    var inAnchorGroup = actor.inAnchorGroup;
    var outAnchorGroup = actor.outAnchorGroup;

    var text = new PointText();
    text.justification = 'right';
    text.fillColor = 'black';
    text.content = input;

    var textWidth = text.bounds.width;
    var textHeight = text.bounds.height + 2;
    var padding = 20;

    shapeGroup.addChild(text);


    if (textWidth > boundingRectangle.width)
        boundingRectangle.right = boundingRectangle.left + textWidth;

    text.position = new Point(boundingRectangle.left + textWidth/2, boundingRectangle.bottom + textHeight/2);

    boundingRectangle.bottom += textHeight;

    shape.size = boundingRectangle.size + padding;
    shape.position = boundingRectangle.center;

    var circle = new Path.Circle(new Point(boundingRectangle.left - padding/2, boundingRectangle.bottom - textHeight/2) ,5);
    circle.strokeColor = 'black';
    circle.fillColor = 'white';
    circle.data.type = 'anchor';
    circle.data.direction = 'in';

    circle.data.param = input;

    outAnchorGroup.children.forEach(function(child) {
        child.position = new Point(boundingRectangle.right + padding/2, child.position.y);
    });

    inAnchorGroup.children.forEach(function(child) {
        child.position = new Point(boundingRectangle.left - padding/2, child.position.y);
    });

    inAnchorGroup.addChild(circle);

    return;
}

function addOutput(actor, output) {
    var boundingRectangle = actor.boundingRectangle;
    var shape = actor.shape;
    var shapeGroup = actor.shapeGroup;

    var inAnchorGroup = actor.inAnchorGroup;
    var outAnchorGroup = actor.outAnchorGroup;

    var title = new PointText();
    title.justification = 'left';
    title.fillColor = 'black';
    title.content = output;

    var textWidth = title.bounds.width;
    var textHeight = title.bounds.height + 2;
    var padding = 20;

    shapeGroup.addChild(title);

    title.position = new Point(boundingRectangle.right - textWidth/2, boundingRectangle.bottom + textHeight/2);

    if (textWidth > boundingRectangle.width)
        boundingRectangle.right = boundingRectangle.left + textWidth;

    boundingRectangle.bottom += textHeight;

    shape.size = boundingRectangle.size + padding;
    shape.position = boundingRectangle.center;

    var circle = new Path.Circle(new Point(boundingRectangle.right + padding/2, boundingRectangle.bottom - textHeight/2) ,5);
    circle.strokeColor = 'black';
    circle.fillColor = 'white';
    circle.data.type = 'anchor';
    circle.data.direction = 'out';

    circle.data.param = output;

    outAnchorGroup.children.forEach(function(child) {
        child.position = new Point(boundingRectangle.right + padding/2, child.position.y);
    });

    inAnchorGroup.children.forEach(function(child) {
        child.position = new Point(boundingRectangle.left - padding/2, child.position.y);
    });

    outAnchorGroup.addChild(circle);

    return;
}

var selected, myPath;
var source, target, from, to;
function onMouseDown(event) {

    var hitResult = project.hitTest(event.point, hitOptions);
    if (!hitResult) {
        //var text = prompt("enter text");
        //addText(shape, "test");

        return;
    }

    console.log(hitResult.item)

    if (!(hitResult.item.data.type == 'anchor')) {
        selected = hitResult.item;
        if (Key.isDown('d')) {
            var actor = selected.parent.data.actor;
            var i = actors.indexOf(actor);
            if (i > -1) {
                actors.splice(i, 1);
            }
            selected.parent.remove();
            selected = null;
            console.log(actors.length)
        }
    } else {
        source = hitResult.item.parent.parent.data.actor;

        if (hitResult.item.data.direction == 'out') {
            myPath = new Path();
            myPath.strokeColor = 'black';
            myPath.add(hitResult.item.position);
            myPath.add(hitResult.item.position);
            myPath.sendToBack();

            source.outputPaths.push(myPath);
            from = hitResult.item.data.param;
            console.log('source: ' + source.name + " - " + to);
        } else {
            selectedPath = hitResult.item.data.connection;
        }
    }
}

function onMouseDrag(event) {
    if (selected) {
        var actor = selected.parent.data.actor;
        var boundingRectangle = actor.boundingRectangle;
        selected.parent.position += event.delta;
        boundingRectangle.point += event.delta;

        actor.outputPaths.forEach(function(path) {
            path.firstSegment.point += event.delta;
            path.lastSegment.handleIn = path.bounds.bottomCenter - path.bounds.bottomRight;
            path.firstSegment.handleOut = path.bounds.topCenter - path.bounds.topLeft;
        });

        actor.inputPaths.forEach(function(path) {
            path.lastSegment.point += event.delta;
            path.lastSegment.handleIn = path.bounds.bottomCenter - path.bounds.bottomRight;
            path.firstSegment.handleOut = path.bounds.topCenter - path.bounds.topLeft;
        });
    }
    if (myPath) {
        // making connection
        myPath.lastSegment.point += event.delta;
        myPath.lastSegment.handleIn = myPath.bounds.bottomCenter - myPath.bounds.bottomRight;
        myPath.firstSegment.handleOut = myPath.bounds.topCenter - myPath.bounds.topLeft;
    }
    if (selectedPath) {
        // removing connection
        var path = selectedPath.path;
        path.lastSegment.point += event.delta;
        path.lastSegment.handleIn = path.bounds.bottomCenter - path.bounds.bottomRight;
        path.firstSegment.handleOut = path.bounds.topCenter - path.bounds.topLeft;
    }
}

var selectedPath;
function onMouseUp(event) {

    var hitResult = project.hitTest(event.point, hitOptions);
    if (!hitResult) {
        if (myPath) myPath.remove();
        source = null;
        target = null;
        from = null;
        to = null;

        return;
    }

    if (hitResult.item.data.type == 'anchor' && hitResult.item.data.direction == 'in') {
        myPath.lastSegment.position = hitResult.item.position;
        var target = hitResult.item.parent.parent.data.actor;
        target.inputPaths.push(myPath);
        to = hitResult.item.data.param;
        console.log('target: ' + target.name + " - " + from);

        //create connection
        connection = {
            source : source,
            target : target.name,
            from : from,
            to : to,
            path : myPath,

        }

        source.connections.push(connection);
        //target.connections.push(connection);
        hitResult.item.data.connection = connection;

        source = null;
        target = null;
        from = null;
        to = null;
    } else if (myPath != null) {
        myPath.remove();
    }

    if (selectedPath) {
        selectedPath.path.remove();
        var i = selectedPath.source.connections.indexOf(selectedPath);
        if (i > -1) {
            selectedPath.source.connections.splice(i, 1);
        }

        selectedPath = null;
    }

    selected = null;

    myPath = null;

}

var actor1 = {
    name : 'Actor with a very long name',
    outputs : ['output1', 'output2', 'output3'],
    inputs : ['input'],
    inputPaths : [],
    outputPaths : [],
    connections : []
}

var actor2 = {
    name : 'Actor2',
    outputs : ['output'],
    inputs : ['input with a very long name', 'input2'],
    inputPaths : [],
    outputPaths : [],
    connections : []
}

var actor3;

var actors = [];
createRectangle(new Point(201, 201), actor1);

createRectangle(new Point(600, 400), actor2);

function onKeyUp(event) {
    if (event.key == 'a') {
        actor3 = {
            name : 'Actor3 with long name',
            outputs : ['output1', 'output2', 'output3'],
            inputs : ['input'],
            inputPaths : [],
            outputPaths : [],
            connections : []
        }

        createRectangle(new Point(100,100), actor3);
    } else if (event.key == 'c') {
        actors.forEach(function(item) {
            item.connections.forEach(function(conn) {
                console.log(conn.source.name + "." + conn.from + " -> " + conn.target + "." + conn.to);
            });

        });
    } else if (event.key == 'l') {
        actors.forEach(function(item) {
            console.log(item);
        });
    }
}