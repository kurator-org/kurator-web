define([
    'jquery',
    'underscore',
    'backbone',
    'models/run',
    'text!templates/run.html'
], function ($, _, Backbone, Run, runTpl) {

    var RunView = Backbone.View.extend({
        tagName: 'tr',
        template: _.template(runTpl),

        events: {

        },

        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
        },

        render: function () {
            var run = this.model.toJSON();

            var $status = $('<span>');

            switch (run.status) {
                case "RUNNING":
                    $status.addClass('label')
                        .addClass('label-default')
                        .html('Complete')
                        .css('font-size', '0.9em');
                    break;
                case "SUCCESS":
                    $status.addClass('label')
                        .addClass('label-success')
                        .html('Complete')
                        .css('font-size', '0.9em');
                    break;
                case "ERRORS":
                    $status.addClass('label')
                        .addClass('label-danger')
                        .html('Complete')
                        .css('font-size', '0.9em');
                    break;
                default:
                    console.log('status');
            }

            this.$el.html(this.template({ run: run, jsRoutes: jsRoutes }));
            this.$('.run-status').append($status);

            return this;
        }

    });

    return RunView;
});