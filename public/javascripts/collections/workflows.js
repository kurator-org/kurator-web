define([
    'underscore',
    'backbone',
    'models/workflow'
], function (_, Backbone, Workflow) {
    var Workflows = Backbone.Collection.extend({
        url: jsRoutes.controllers.Workflows.list().url,

        modelId: function (attrs) {
            return attrs.name;
        }
    });

    return Workflows;
});