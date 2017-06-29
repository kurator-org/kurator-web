define([
    'jquery',
    'underscore',
    'backbone',
    'collections/runs',
    'text!templates/runstatus.html'
], function ($, _, Backbone, Runs, runstatusTpl) {

    var RunStatusView = Backbone.View.extend({
        template: _.template(runstatusTpl),

        events: {

        },

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({runs: this.collection.toJSON()}));

            return this;
        }

    });

    return RunStatusView;
});