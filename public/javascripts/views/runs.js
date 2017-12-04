define([
    'jquery',
    'underscore',
    'backbone',
    'models/result',
    'views/result',
    'views/run',
    'views/sharerun',
    'text!templates/runs.html'
], function ($, _, Backbone, ResultModel, ResultView, RunView, ShareView, runsTpl) {

    var RunsView = Backbone.View.extend({
        template: _.template(runsTpl),

        initialize: function (options) {
            this.options = options;

            this.views = [];
            this.selected = [];

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();

            var that = this;
            this.timer = setInterval(function() {
                that.collection.fetch();
            }, 5000);
        },

        render: function () {
            // clean up the subviews before rendering new ones
            _.invoke(this.views, 'destroy');
            this.views.length = 0;

            this.$el.html(this.template({ }));

            this.collection.each(function (run) {
                //if (run.get('owner').id == app.session.get('uid')) {
                if (run.get('owner').id == this.options.uid) {
                    if (this.selected.includes(run)) {
                        run.set('selected', true);
                    }

                    this.addRun(run);
                }
            }, this);

            return this;
        },

        addRun: function (run) {
            var view = new RunView({ model: run });
            this.listenTo(view, 'runChecked', this.runChecked);
            this.listenTo(view, 'viewResult', this.viewResult);

            this.views.push(view);
            this.trigger('addedUserRun', { count: this.views.length });

            this.$('tbody').append(view.render().el);
        },

        removeSelected: function () {
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

        selectAll: function () {
            this.views.forEach(function(run) {
                run.setSelected(true);
            });
        },

        selectNone: function () {
            this.views.forEach(function(run) {
                run.setSelected(false);
            });
        },

        shareRuns: function (evt) {
            var view = new ShareView({ collection: this.collection, selected: this.selected });
            $('#dialog').html(view.$el);
        },

        runChecked: function (evt) {
            if (evt.get('selected')) {
                this.selected.push(evt.id);
            } else {
                this.selected.pop(evt.id);
            }

            this.trigger('selectionChange', this.selected);
        },

        viewResult: function (run) {
            var view = new ResultView({ run: run });
            $('#dialog').html(view.$el);
        },

        onBeforeClose: function () {
            if (this.timer) {
                clearInterval(this.timer);
            }
        }
    });

    return RunsView;
});