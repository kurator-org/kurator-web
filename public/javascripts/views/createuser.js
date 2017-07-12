define([
    'jquery',
    'underscore',
    'backbone',
    'collections/users',
    'text!templates/createuser.html'
], function ($, _, Backbone, UserCollection, createUserTpl) {

    var CreateUserView = Backbone.View.extend({
        template: _.template(createUserTpl),

        events: {
            'hidden.bs.modal .modal': 'close',
            'click #user-submit-btn': 'createUser'
        },

        initialize: function () {
            this.collection = new UserCollection();

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({}));
            this.$('.modal').modal('show');

            return this;
        },

        createUser: function (e) {
            console.log('create user...');
            // TODO: form submit to create user
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

    return CreateUserView;
});