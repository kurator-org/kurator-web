define([
    'jquery',
    'underscore',
    'backbone',
    'views/sharedrun',
    'views/result',
    'text!templates/sharedruns.html'
], function ($, _, Backbone, SharedRunView, ResultView, sharedTpl) {

    var SharedRunsView = Backbone.View.extend({
        template: _.template(sharedTpl),

        initialize: function (options) {
            this.options = options;

            this.views = [];
            this.count = 0;

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
                if (!(run.get('owner').id == this.options.uid)) {
                    this.addRun(run);
                }
            }, this);

            return this;
        },

        addRun: function (run) {
            var view = new SharedRunView({ model: run });
            this.listenTo(view, 'viewResult', this.viewResult);

            this.views.push(view);
            this.trigger('addedSharedRun', { count: this.views.length });

            this.$('tbody').append(view.render().el);
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

    return SharedRunsView;
});