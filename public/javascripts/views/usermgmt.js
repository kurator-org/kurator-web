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

        initialize: function() {
            //this.listenTo(this.collection, 'add', this.addUser);

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();

            // TODO: hardcoded for now to test UI
            this.json = {
                'data': [
                    {
                        'text': '<i>User Management</i>',
                        'state': {
                            'opened': true,
                        },
                        'children': [
                            {
                                'text': '<b>All Users</b>',
                                'icon': 'glyphicon glyphicon-globe',
                                'state': {
                                    'selected': true
                                }
                            },
                            {
                                'text': 'Active',
                                'icon': ' glyphicon glyphicon-ok',
                            },
                            {
                                'text': 'Inactive',
                                'icon': 'glyphicon glyphicon-remove',
                            },
                            {
                                'text': '<i>Roles</i>',
                                'state': {
                                    'opened': true
                                },
                                'children': [
                                    {
                                        'text': 'ADMIN',
                                        "icon": "glyphicon glyphicon-user"
                                    },
                                    {
                                        'text': 'INSTRUCTOR',
                                        "icon": "glyphicon glyphicon-user"
                                    },
                                    {
                                        'text': 'USER',
                                        "icon": "glyphicon glyphicon-user"
                                    }
                                ]
                            },
                            {
                                'text': '<i>Groups</i>',
                                'state': {
                                    'opened': true
                                },
                                'children': [
                                    {
                                        'text': 'Guest Accounts',
                                        'icon': ' glyphicon glyphicon-folder-close'
                                    },
                                    {
                                        'text': 'Education & Outreach',
                                        'icon': ' glyphicon glyphicon-folder-close'
                                    },
                                    {
                                        'text': 'SPNHC',
                                        'icon': ' glyphicon glyphicon-folder-close'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            };
        },

        addUser: function (user) {
            var view = new UserView({ model: user });
            this.$('#user-table').append(view.render().el);
        },

        render: function() {
            this.$el.html(this.template());

            this.$('#side-bar').jstree({ 'core' : this.json });

            this.collection.each(function (user) {
                this.addUser(user);
            }, this);

            return this;
        }
    });

    return UserManagementView;
});