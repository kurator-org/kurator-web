define([
    'underscore',
    'backbone',
    'models/run'
], function (_, Backbone, Run) {
    var Runs = Backbone.Collection.extend({
        model: Run
    });

    return Runs;
});