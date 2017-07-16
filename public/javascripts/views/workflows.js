define([
    'jquery',
    'underscore',
    'backbone',
    'app',
    'text!templates/workflow.html'
], function ($, _, Backbone, app, workflowTpl) {
    var WorkflowsView = Backbone.View.extend({
        template: _.template(workflowTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({definitions : this.collection.toJSON(), infoImg : app.assetsUrl + "images/info.png"}));

            return this;
        }
    });

    return WorkflowsView;
});