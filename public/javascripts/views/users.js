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
            'click .page-btn': 'viewPage'
        },

        initialize: function() {
            this.collection = app.currentUsers;

            this.listenTo(this.collection, 'sync', this.render);
            this.collection.fetch();

            this.filter = { }; // all users
            this.currPage = 0;
        },

        addUser: function (user) {
            var view = new UserView({ model: user });
            this.listenTo(view, 'dragging', this.draggingUser);
            this.listenTo(view, 'dropped', this.droppedUser);
            // TODO: dispose of views at some point?

            this.$('#user-table tbody').append(view.render().el);
        },

        render: function() {
            users = [ ];

            // apply the filter to the collection and display
            this.collection.each(function (user) {

                if (this.filter.groupId) {
                    var groupId = this.filter.groupId;
                    var group = app.currentGroups.get(groupId);

                    if (user.hasGroup(group)) {
                        users.push(user);
                    }
                } else if (this.filter.role) {
                    var role = this.filter.role;

                    if (user.get('role') == role) {
                        users.push(user);
                    }
                } else if('active' in this.filter) {
                    var active = this.filter.active;

                    if (user.get('active') == active) {
                        users.push(user);
                    }
                } else {
                    // display all users
                    users.push(user);
                }

            }, this);

            var total = users.length;
            var pages = total/10;

            this.$el.html(this.template({ total : users.length, pages: pages }));

            var start = (this.currPage * 10);
            var limit = (start + 10) < users.length ? 10 : users.length - start;

            for (i = 0; i < limit; i++) {
                var user = users[start + i];
                this.addUser(user);
            }

            $('.start').html(start+1);
            $('.limit').html(start+limit);

            // Set total user count
            return this;
        },

        draggingUser: function (user) {
            this.trigger('dragging', user);
        },

        droppedUser: function (user) {
            this.trigger('dropped', user);
        },

        viewPage: function (e) {
            this.currPage = $(e.target).attr('id');
            this.render();
        }
    });

    return UserTableView;
});