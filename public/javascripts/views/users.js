define([
    'jquery',
    'underscore',
    'backbone',
    'views/user',
    'app',
    'text!templates/users.html'
], function ($, _, Backbone, UserView, app, usersTpl) {
    var UserTableView = Backbone.View.extend({
        template: _.template(usersTpl),

        events: {

        },

        initialize: function() {
            this.collection = app.currentUsers;

            this.listenTo(this.collection, 'sync', this.render);
            this.collection.fetch();

            this.filter = { }; // all users
        },

        addUser: function (user) {
            var view = new UserView({ model: user });
            this.listenTo(view, 'dragging', this.draggingUser);
            this.listenTo(view, 'dropped', this.droppedUser);
            // TODO: dispose of views at some point?

            this.$('#user-table').append(view.render().el);
        },

        render: function() {
            this.$el.html(this.template({ }));

            // apply the filter to the collection and display
            this.collection.each(function (user) {

                if (this.filter.groupId) {
                    var groupId = this.filter.groupId;
                    var group = app.currentGroups.get(groupId);

                    if (user.hasGroup(group)) {
                        this.addUser(user);
                    }
                } else if (this.filter.role) {
                    var role = this.filter.role;

                    if (user.get('role') == role) {
                        this.addUser(user);
                    }
                } else if('active' in this.filter) {
                    var active = this.filter.active;
                    console.log(user.get('active'));
                    if (user.get('active') == active) {
                        this.addUser(user);
                    }
                } else {
                    // display all users
                    this.addUser(user);
                }

            }, this);

            return this;
        },

        draggingUser: function (user) {
            this.trigger('dragging', user);
        },

        droppedUser: function (user) {
            this.trigger('dropped', user);
        }
    });

    return UserTableView;
});