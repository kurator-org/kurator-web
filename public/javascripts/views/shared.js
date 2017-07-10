define([
    'jquery',
    'underscore',
    'backbone',
    'text!templates/shared.html'
], function ($, _, Backbone, sharedTpl) {

    var SharedRunsView = Backbone.View.extend({
        template: _.template(sharedTpl),

        initialize: function () {
            //this.listenTo(this.collection, 'update', this.render);
            //this.collection.fetch();

            this.render();
        },

        render: function () {
            this.$el.html(this.template({}));

            return this;
        }
    });

    return SharedRunsView;
});