define([
    'jquery',
    'underscore',
    'backbone',
    'jstree',
    'views/user',
    'text!templates/users.html'
], function ($, _, Backbone, jstree, UserView, usersTpl) {
    var UserManagementView = Backbone.View.extend({
        template: _.template(usersTpl),

        events: {
            'mouseup .user-group': 'mouseUpNode'
        },

        initialize: function() {
            //this.listenTo(this.collection, 'add', this.addUser);

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();

            // TODO: hardcoded for now to test UI
            this.groups = [
                { 'id': 0, 'name': 'Guest Accounts' },
                { 'id': 1, 'name': 'Education & Outreach' },
                { 'id': 2, 'name': 'SPNHC' },
            ];
        },

        addUser: function (user) {
            var view = new UserView({ model: user });
            this.listenTo(view, 'dragging', this.draggingUser);
            this.listenTo(view, 'dropped', this.droppedUser);

            this.$('#user-table').append(view.render().el);
        },

        render: function() {
            this.$el.html(this.template({ groups: this.groups }));

            this.$tree = this.$('#tree-view').jstree();

            this.collection.each(function (user) {
                this.addUser(user);
            }, this);

            return this;
        },

        draggingUser: function (user) {
            this.dragUser = user;
            console.log(this);
        },

        droppedUser: function (user) {
            if (this.targetGroup) {
                var group = $(this.targetGroup).attr('value');
                var user = user.get('id');
                console.log('add user: ' + user + ' to group: ' + group);

                delete this.targetGroup;
            }

            delete this.dragUser;
        },

        mouseUpNode: function (node) {
            if (this.dragUser) {
                this.targetGroup = node.currentTarget;
            }
            console.log(this);
        }
    });

    return UserManagementView;
});