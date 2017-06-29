define([
    'jquery',
    'underscore',
    'backbone',
    'models/run'
], function ($, _, Backbone, Run) {

    var RunView = Backbone.View.extend({
        template: _.template(runTpl),

        events: {

        },

        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
        },

        render: function () {
            this.$el.html(this.template({run: this.run.toJSON()}));

            return this;
        }

    });

    return RunView;
});