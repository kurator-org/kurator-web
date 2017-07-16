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
        },

        comparator: function(a, b) {
            a = new Date(a.attributes.startTime);
            b = new Date(b.attributes.startTime);
            return a > b ? -1 : a < b ? 1 : 0;
        }
    });

    return Runs;
});