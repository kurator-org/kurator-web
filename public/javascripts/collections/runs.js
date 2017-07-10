define([
    'underscore',
    'backbone',
    'models/run'
], function (_, Backbone, Run) {
    var Runs = Backbone.Collection.extend({
        model: Run,

        initialize: function (options) {
            this.options = options;
        },

        url: function () {
            return jsRoutes.controllers.Workflows.status(this.options.uid).url;
        }
    });

    return Runs;
});