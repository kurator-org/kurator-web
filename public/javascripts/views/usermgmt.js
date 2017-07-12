define([
    'jquery',
    'underscore',
    'backbone',
    'jstree',
    'views/user',
    'views/treelist',
    'views/creategroup',
    'views/createuser',
    'text!templates/users.html'
], function ($, _, Backbone, jstree, UserView, TreeListView, CreateGroupView, CreateUserView, usersTpl) {
    var UserManagementView = Backbone.View.extend({
        template: _.template(usersTpl),

        events: {
            'click .create-group-btn': 'createGroup',
            'click .create-user-btn': 'createUser',
            'click .add-to-group-btn': 'addToGroup'
        },

        initialize: function() {
            //this.listenTo(this.collection, 'add', this.addUser);

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();

            // TODO: hardcoded for now to test UI
        },

        addUser: function (user) {
            var view = new UserView({ model: user });
            this.listenTo(view, 'dragging', this.draggingUser);
            this.listenTo(view, 'dropped', this.droppedUser);

            this.$('#user-table').append(view.render().el);
        },

        render: function() {
            this.$el.html(this.template({ groups: this.groups }));

            this.treeView = new TreeListView();
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
            console.log('add to group');
            // TODO: implement this on backend and use http PUT
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