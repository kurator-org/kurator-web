define([
    'jquery',
    'underscore',
    'backbone',
    'views/sharedrun',
    'app',
    'text!templates/sharedruns.html'
], function ($, _, Backbone, SharedRunView, app, sharedTpl) {

    var SharedRunsView = Backbone.View.extend({
        template: _.template(sharedTpl),

        initialize: function () {
            this.views = [];
            this.count = 0;

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();

            var that = this;
            this.timer = setInterval(function() {
                console.log('polling...');
                that.collection.fetch();
            }, 5000);
        },

        render: function () {
            this.$el.html(this.template({ }));

            this.collection.each(function (run) {
                if (!(run.get('owner').id == app.session.get('uid'))) {
                    this.addRun(run);
                }
            }, this);

            return this;
        },

        addRun: function (run) {
            var view = new SharedRunView({ model: run });
            this.views.push(view);

            this.$('tbody').append(view.render().el);
            this.trigger('addedRun', { count: this.views.length });
        },

        onBeforeClose: function () {
            if (this.timer) {
                clearInterval(this.timer);
            }
        }
    });

    return SharedRunsView;
});