define([
    'jquery',
    'underscore',
    'backbone',
    'models/run',
    'text!templates/sharedrun.html'
], function ($, _, Backbone, Run, sharedRunTpl) {

    var RunView = Backbone.View.extend({
        tagName: 'tr',
        template: _.template(sharedRunTpl),

        events: {
            'click  .result-btn': 'viewResult'
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
                        .html('Running')
                        .css('font-size', '0.9em');
                    break;
                case "SUCCESS":
                    $status.addClass('label')
                        .addClass('label-success')
                        .html('Success')
                        .css('font-size', '0.9em');
                    break;
                case "ERRORS":
                    $status.addClass('label')
                        .addClass('label-danger')
                        .html('Errors')
                        .css('font-size', '0.9em');
                    break;
                default:
                    console.log('status');
            }

            this.$el.html(this.template({ run: run }));
            this.$('.run-status').append($status);

            return this;
        },
        
        toggleSelected: function (evt) {
            this.model.set('selected', $(evt.target).prop('checked'));
            this.trigger("runChecked", this.model);
        },

        viewResult: function (evt) {
            this.trigger("viewResult", this.model);
        }
    });

    return RunView;
});