define([
    'jquery',
    'underscore',
    'backbone',
    'views/user',
    'text!templates/users.html'
], function ($, _, Backbone, UserView, usersTpl) {
    var UserManagementView = Backbone.View.extend({
        template: _.template(usersTpl),

        initialize: function() {
            //this.listenTo(this.collection, 'add', this.addUser);

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        addUser: function (user) {
            var view = new UserView({ model: user });
            this.$('#user-table').append(view.render().el);
        },

        render: function() {
            this.$el.html(this.template());

            var that = this;

            this.collection.each(function (user) {
                that.addUser(user);
            });

            return this;
        }
    });

    return UserManagementView;
});