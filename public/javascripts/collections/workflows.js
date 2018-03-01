define([
    'underscore',
    'backbone',
    'models/workflow'
], function (_, Backbone, Workflow) {
    var Workflows = Backbone.Collection.extend({
        initialize: function (search, input, dwcclass) {
            this.url = jsRoutes.controllers.Workflows.list(search, input, dwcclass).url
        },

        modelId: function (attrs) {
            return attrs.name;
        }
    });

    return Workflows;
});