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
            'click .remove-btn': 'removeRuns',
            'click .share-btn': 'shareResults',
            'shown.bs.tab a[data-toggle="tab"]': 'toggleTab'
        },

        initialize: function () {
            this.selected = [];
            this.activeTab = '#user-runs';

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();

            var that = this;
            this.timer = setInterval(function() {
                that.collection.fetch();
            }, 5000);
        },

        render: function () {
            console.log('render...');
            this.$el.html(this.template({runs: this.collection.toJSON()}));

            this.collection.each(function (run) {
                if (this.selected.includes(run)) {
                    run.set('selected', true);
                }

                this.addRun(run);
            }, this);

            this.$('a[href="' + this.activeTab + '"]').tab('show');

            return this;
        },

        addRun: function (run) {
            var view = new RunView({ model: run });
            this.listenTo(view, 'runChecked', this.runChecked);

            this.$('#user-runs').find('tbody').append(view.render().el);
        },

        removeRuns: function (evt) {
            var runs = [];
            this.collection.where({selected: true}).forEach(function (run) {
                runs.push(run.toJSON());
            });

            var that = this;
            var onSuccess = function(data) {
                console.log(data);
                that.collection.remove(data);
            };

            $.ajax({
                type: "POST",
                url: jsRoutes.controllers.Workflows.removeRuns().url,
                data: JSON.stringify({ runs: runs }),
                dataType: "json",
                contentType: "application/json",
                success: onSuccess
                //dataType: dataType
            });
        },

        toggleTab: function (evt) {
            this.activeTab = $(evt.target).attr('href');
        },

        shareResults: function (evt) {

        },

        runChecked: function (evt) {
            if (evt.get('selected')) {
                this.selected.push(evt.id);
            } else {
                this.selected.pop(evt.id);
            }

            // TODO: should this be a separate view?
            if (this.selected.length) {
                this.$('#run-controls button').prop('disabled', false);
            } else {
                this.$('#run-controls button').prop('disabled', true);
            }
        }



    });

    return RunStatusView;
});