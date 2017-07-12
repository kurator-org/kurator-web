define([
    'jquery',
    'underscore',
    'backbone',
    'collections/groups',
    'text!templates/creategroup.html'
], function ($, _, Backbone, GroupCollection, createGroupTpl) {

    var CreateGroupView = Backbone.View.extend({
        template: _.template(createGroupTpl),

        events: {
            'hidden.bs.modal .modal': 'close',
            'click #group-submit-btn': 'createGroup'
        },

        initialize: function () {
            this.collection = new GroupCollection();

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({}));
            this.$('.modal').modal('show');

            return this;
        },

        createGroup: function (e) {
            console.log('create group...');
            // TODO: form submit to create group
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

    return CreateGroupView;
});