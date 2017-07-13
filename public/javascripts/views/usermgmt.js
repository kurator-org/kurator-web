define([
    'jquery',
    'underscore',
    'backbone',
    'jstree',
    'views/user',
    'views/treelist',
    'views/creategroup',
    'views/createuser',
    'app',
    'text!templates/users.html'
], function ($, _, Backbone, jstree, UserView, TreeListView, CreateGroupView, CreateUserView, app, usersTpl) {
    var UserManagementView = Backbone.View.extend({
        template: _.template(usersTpl),

        events: {
            'click .create-group-btn': 'createGroup',
            'click .create-user-btn': 'createUser',
            'click .add-to-group-btn': 'addToGroup'
        },

        initialize: function() {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        addUser: function (user) {
            var view = new UserView({ model: user });
            this.listenTo(view, 'dragging', this.draggingUser);
            this.listenTo(view, 'dropped', this.droppedUser);

            this.$('#user-table').append(view.render().el);
        },

        render: function() {
            this.$el.html(this.template({ }));

            this.treeView = new TreeListView({ collection: app.currentGroups });
            this.listenTo(this.treeView, 'usermoved', this.addToGroup);

            this.$('#side-bar').html(this.treeView.el);

            this.collection.each(function (user) {
                this.addUser(user);
            }, this);

            return this;
        },
        
        createGroup: function (e) {
            console.log('create group');
            var view = new CreateGroupView();
            $('#dialog').html(view.$el);
        },
        
        createUser: function (e) {
            console.log('create user');
            var view = new CreateUserView();
            $('#dialog').html(view.$el);
        },
        
        addToGroup: function (e) {
            $.ajax({
                type: "POST",
                url: jsRoutes.controllers.Users.addUserToGroup().url,
                // The key needs to match your method's input parameter (case-sensitive).
                data: JSON.stringify({ user: e.user, group: e.group }),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                success: function(data){console.log(data);},
                failure: function(errMsg) {
                    console.log(errMsg);
                }
            });
        },

        draggingUser: function (user) {
            this.treeView.draggingUser(user);
        },

        droppedUser: function (user) {
            this.treeView.droppedUser(user);
        }
    });

    return UserManagementView;
});