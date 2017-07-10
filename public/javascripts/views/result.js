define([
    'jquery',
    'underscore',
    'backbone',
    'models/result',
    'text!templates/result.html'
], function ($, _, Backbone, ResultModel, resultTpl) {

    var ResultView = Backbone.View.extend({
        template: _.template(resultTpl),

        events: {
            'hidden.bs.modal #result-modal': 'close'
        },

        initialize: function (options) {
            this.model = new ResultModel({ id: options.run.id });

            this.listenTo(this.model, 'change', this.render);
            this.model.fetch();
        },

        render: function () {
            this.$el.html(this.template(this.model.toJSON()));
            this.$('#result-modal').modal('show');

            return this;
        },

        close: function() {
            // Unbind from events
            this.undelegateEvents();

            this.$el.removeData().unbind();

            // Remove view from DOM
            this.remove();
            Backbone.View.prototype.remove.call(this);
        }
    });

    return ResultView;
});