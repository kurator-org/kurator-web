actor1 = {
    name : 'actor',
    title : 'Actor',
    inputs : [
        { param : 'input1', label : 'Input1' },
        { param : 'input2', label : 'Input2' },
        { param : 'input3', label : 'Input3' }
    ],
    outputs : [
        { param : 'output1', label : 'Output1' },
        { param : 'output2', label : 'Output2' }
    ],
    connections : [],
}

function addActor(actor, position) {

}

function createActor(actor, position) {
    var hSpacing = 20, vSpacing = 10, padding = 10;
    //var boundingRect = new Rectangle(position); // component bounding rectangle

    // group contains all actor components
    var actorComponents = new Group([]);

    // create actor rectangle
    var actorShape = new Shape.Rectangle();
    actorShape.strokeColor = 'black';
    actorShape.fillColor = '#167ee5';

    actorComponents.addChild(actorShape);

    // create title rectangle
    var title = createTitle(actor.title);
    actorComponents.addChild(title);

    // create input and output endpoints
    var outputs = createEndpoints(actor.outputs, 'out');
    var inputs = createEndpoints(actor.inputs, 'in');
    actorComponents.addChild(outputs);
    actorComponents.addChild(inputs);

    // calculate width and height of actor rectangle and subcomponents
    var titleWidth = title.bounds.width + padding*2;
    var contentWidth = inputs.bounds.width + outputs.bounds.width + hSpacing + padding;
    var contentHeight = inputs.bounds.height + outputs.bounds.height + vSpacing + padding;

    var width = Math.max(contentWidth, titleWidth)

    actorShape.size = new Size(width, contentHeight);
    actorShape.position = position;

    // position title
    if (titleWidth > contentWidth) {
        // center the title if wider than content
        title.position = actorShape.bounds.topCenter;
    } else {
        // otherwise position the title slightly offset from the left
        var offset = 10;

        var x = (actorShape.bounds.left + title.bounds.width/2) + offset;
        var y = actorShape.bounds.top;

        title.position = new Point(x, y);
    }

    // position parameter text
    var left = actorShape.bounds.left;
    var right = actorShape.bounds.right;
    var top = actorShape.bounds.top;
    var bottom = actorShape.bounds.bottom;

    inputs.position = new Point(left+(inputs.bounds.width/2)+padding, bottom-(inputs.bounds.height/2+padding));
    outputs.position = new Point(right-(outputs.bounds.width/2+padding), top+(outputs.bounds.height/2+padding))

    // create and position endpoint anchors
    var inAnchors = new Group([]);
    inputs.children.forEach(function(child) {
        var textMiddle = child.bounds.center.y;

        var anchor = new Path.Circle(new Point(left, textMiddle), 5);
        anchor.strokeColor = 'black';
        anchor.fillColor = 'white';
        inAnchors.addChild(anchor);
    });

    actorComponents.addChild(inAnchors);

    var outAnchors = new Group([]);
    outputs.children.forEach(function(child) {
        var textMiddle = child.bounds.center.y;

        var anchor = new Path.Circle(new Point(right, textMiddle), 5);
        anchor.strokeColor = 'black';
        anchor.fillColor = 'white';
        outAnchors.addChild(anchor);
    });

    actorComponents.addChild(outAnchors);

    return actorComponents;
}

function createTitle(title) {
    var padding = 5;

    var titleText = new PointText();
    titleText.justification = 'center';
    titleText.fillColor = 'black';
    titleText.content = title;

    var width = titleText.bounds.width + padding;
    var height = titleText.bounds.height + padding;

    var titleShape = new Shape.Rectangle(titleText.bounds);
    titleShape.strokeColor = 'black';
    titleShape.fillColor = 'white';

    titleShape.size = new Size(width + padding, height + padding);
    return new Group([titleShape, titleText]);
}

function createEndpoints(items, type) {
    var spacing = 5; // vertical spacing

    var prev, endpoints = new Group([]);
    items.forEach(function(item) {
        var curr = createEndpoint(item.label, type);

        // position endpoint below previous
        if (prev)
            curr.position.y += prev.position.y + prev.bounds.height + spacing;

        prev = curr;
        endpoints.addChild(curr); // add current endpoint to group
    });

    return endpoints;
}

function createEndpoint(label, type) {
    // parameter label
    var text = new PointText(new Point(0,0));
    text.justification = 'left';
    text.fillColor = 'black';
    text.content = label;

    var textWidth = text.bounds.width;
    var textHeight = text.bounds.height;
    var textMiddle = text.bounds.leftCenter.y;

    // position connector to left/right of text
    //var position, radius = 5, spacing = 10;
    //if (type === 'out')
    //    position = new Point(textWidth + spacing, textMiddle);
    //else if (type === 'in')
    //    position = new Point(-spacing, textMiddle);

    // endpoint connector
    //var endpoint = new Path.Circle(position, 5);
    //endpoint.strokeColor = 'black';
    //endpoint.fillColor = 'white';

    return text;
}

//createActor(actor1, new Point(550,550))

//var text = createEndpoints(actor1.inputs, 'in');
//var rect = new Shape.Rectangle(text.bounds);
//rect.strokeColor = 'black';
//rect.position = new Point(550,500);
//text.position = new Point(550,500);

//var title = createTitle("testing");
//var rect = new Shape.Rectangle(title.bounds);
//console.log(title.bounds);

//rect.strokeColor = 'red';
//rect.position = new Point(550,500);
//title.position = new Point(550,500);

//var actor = createActor(actor1, new Point(550, 500));

var hitOptions = {
    segments: true,
    stroke: true,
    fill: true,
    tolerance: 5
};

function onMouseDrag(event) {
    var hitResult = project.hitTest(event.point, hitOptions);
    if (!hitResult)
        return;

    hitResult.item.parent.position += event.delta;
}