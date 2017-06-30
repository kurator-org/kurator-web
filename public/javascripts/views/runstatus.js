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
            //'click .share-btn': 'shareResults' TODO: for sharing of results
        },

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();

            var that = this;
            this.timer = setInterval(function() {
                that.collection.fetch();
            }, 5000);
        },

        render: function () {
            this.$el.html(this.template({runs: this.collection.toJSON()}));

            var selected = this.collection.where({selected: true});

            this.collection.each(function (run) {
                if (selected.includes(run)) {
                    run.set('selected', true);
                }

                this.addRun(run);
            }, this);

            return this;
        },

        addRun: function (run) {
            var view = new RunView({ model: run });
            this.$('tbody').append(view.render().el);
        },

        removeRuns: function (evt) {
            this.collection.where({selected: true}).forEach(function (run) {
                console.log('deleting: ' + run.id);
            });
        },

        shareResults: function (evt) {

        }



    });

    return RunStatusView;
});