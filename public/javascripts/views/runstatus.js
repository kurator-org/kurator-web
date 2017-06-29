define([
    'jquery',
    'underscore',
    'backbone',
    'views/run',
    'text!templates/runs.html'
], function ($, _, Backbone, RunView, statusTpl) {

    var RunStatusView = Backbone.View.extend({
        template: _.template(statusTpl),

        events: {

        },

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({runs: this.collection.toJSON()}));

            this.collection.each(function (run) {
                this.addRun(run);
            }, this);

            return this;
        },

        addRun: function (run) {
            var view = new RunView({ model: run });
            this.$('tbody').append(view.render().el);
        }



    });

    return RunStatusView;
});