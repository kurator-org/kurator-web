define([
    'jquery',
    'underscore',
    'backbone',
    'models/result',
    'views/result',
    'views/run',
    'text!templates/runs.html'
], function ($, _, Backbone, ResultModel, ResultView, RunView, runsTpl) {

    var RunsView = Backbone.View.extend({
        template: _.template(runsTpl),

        initialize: function () {
            this.views = [];
            this.selected = [];

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();

            var that = this;
            this.timer = setInterval(function() {
                console.log('polling...');
                that.collection.fetch();
            }, 5000);
        },

        render: function () {
            console.log('render');
            // clean up the subviews before rendering new ones
            _.invoke(this.views, 'destroy');
            this.views.length = 0;

            this.$el.html(this.template({runs: this.collection.toJSON()}));

            this.collection.each(function (run) {
                if (this.selected.includes(run)) {
                    run.set('selected', true);
                }

                this.addRun(run);
            }, this);

            return this;
        },

        addRun: function (run) {
            var view = new RunView({ model: run });
            this.listenTo(view, 'runChecked', this.runChecked);
            this.listenTo(view, 'viewResult', this.viewResult);

            this.views.push(view);

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

        shareRuns: function (evt) {

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
            $('#dialog').append(view.$el);
        },

        onBeforeClose: function () {
            if (this.timer) {
                clearInterval(this.timer);
            }
        }
    });

    return RunsView;
});