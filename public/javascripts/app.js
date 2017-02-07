define([
    'jquery',
    'underscore',
    'backbone',
    'd3'
], function ($, _, Backbone, d3) {

    // app globals
    var app = { };

    // Global event aggregator
    app.eventAggregator = _.extend({}, Backbone.Events);

    // View.close() event for garbage collection
    Backbone.View.prototype.close = function() {
        this.remove();
        this.unbind();
        if (this.onClose) {
            this.onClose();
        }
    };

    return app;
});
