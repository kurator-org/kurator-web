define([
    'jquery',
    'underscore',
    'backbone',
    'jstree',
    'views/user',
    'views/treelist',
    'views/users',
    'views/creategroup',
    'views/createuser',
    'app',
    'text!templates/usermgmt.html'
], function ($, _, Backbone, jstree, UserView, TreeListView, UserTableView, CreateGroupView, CreateUserView, app, usersTpl) {
    var UserManagementView = Backbone.View.extend({
        template: _.template(usersTpl),

        events: {
            'click .create-group-btn': 'createGroup',
            'click .create-user-btn': 'createUser',
            'click .add-to-group-btn': 'addToGroup'
        },

        initialize: function() {

        },

        render: function() {
            this.$el.html(this.template({ }));

            // Create the tree view
            this.treeView = new TreeListView({ collection: app.currentGroups });
            this.listenTo(this.treeView, 'usermoved', this.addToGroup);
            this.listenTo(this.treeView, 'nodeselected', this.selectFilter);

            this.$('#side-bar').html(this.treeView.el);

            // Create the table view
            this.tableView = new UserTableView({ collection: app.currentUsers });
            this.listenTo(this.tableView, 'dragging', this.draggingUser);
            this.listenTo(this.tableView, 'dropped', this.droppedUser);

            this.$('#content').html(this.tableView.el);

            return this;
        },

        selectFilter: function (filter) {
            console.log('filter: ' + JSON.stringify(filter));

            this.tableView.filter = filter;
            this.tableView.render();
        },
        
        createGroup: function (e) {
            console.log('create group');
            var view = new CreateGroupView();
            $('#dialog').html(view.$el);
        },
        
        createUser: function (e) {
            var view = new CreateUserView();
            $('#dialog').html(view.$el);
        },
        
        addToGroup: function (e) {
            var user = app.currentUsers.get(e.user);
            var group = app.currentGroups.get(e.group);

            // only add if the user isn't already in the group
            if (!user.hasGroup(group)) {
                var groups = user.get('groups');
                groups.push(group);
                //user.set({'groups': groups});

                user.save();
                //app.currentGroups.set(group);
            } else {
                console.log('user already in group...');
            }
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