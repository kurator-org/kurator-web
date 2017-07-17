define([
    'jquery',
    'underscore',
    'backbone',
    'collections/groups',
    'models/run',
    'app',
    'text!templates/sharerun.html'
], function ($, _, Backbone, GroupCollection, Run, app, shareTpl) {

    var ShareView = Backbone.View.extend({
        template: _.template(shareTpl),

        events: {
            'hidden.bs.modal #share-modal': 'close',
            'click #share-submit-btn': 'shareRun',
            'click .group-item': 'addGroup',
            'click .user-item': 'addUser'
        },

        initialize: function (options) {
            this.selected = options.selected;
            console.log(options);
            this.groups = [];
            this.users = [];

            this.listenTo(app.currentGroups, 'update', this.updateGroups);
            this.listenTo(app.currentUsers, 'update', this.updateUsers);

            this.render();
        },

        render: function () {
            this.$el.html(this.template({}));
            this.$('#share-modal').modal('show');

            app.currentGroups.fetch();
            app.currentUsers.fetch();

            return this;
        },

        shareRun: function (e) {
            console.log(this.selected);
            this.collection.each(function (run) {
                if (this.selected.indexOf(run.id) != -1) {
                    run.set('users', this.users);
                    run.set('groups', this.groups);

                    run.save();
                }
            }, this);
        },

        close: function() {
            // Unbind from events
            this.undelegateEvents();

            this.$el.removeData().unbind();

            // Remove view from DOM
            this.remove();
            Backbone.View.prototype.remove.call(this);
        },
        
        updateGroups: function (e) {
            app.currentGroups.each(function (group) {
                $groupItem = $('<li>').append($('<a class="group-item">').attr('href', '#').html(group.get('name'))).attr('value', group.get('id'));

                this.$('.divider').after($groupItem);
            }, this);
        },

        updateUsers: function (e) {
            console.log(app.session);
                app.currentUsers.each(function (user) {
                    if (app.session.get('username') != user.username) {
                        $userItem = $('<li>').append($('<a class="user-item">').attr('href', '#').html(user.get('username'))).attr('value', user.get('id'));

                        this.$('.divider').before($userItem);
                    }
                }, this);
        },
        
        addUser: function (e) {
            var userId = $(e.target).parent().val();
            var user = app.currentUsers.get(userId);
            console.log(this.groups.indexOf(user));

            if (this.users.indexOf(user) == -1) {
                this.users.push(user);
                this.updateShareWith();
            }
        },
        
        addGroup: function (e) {
            var groupId = $(e.target).parent().val();
            var group = app.currentUsers.get(groupId);

            if (this.groups.indexOf(group) == -1) {
                this.groups.push(group);
                this.updateShareWith();
            }
        },

        updateShareWith: function () {
            var value = '';

            this.users.forEach(function(user) {
                if (value.length) {
                    value += ', ';
                }

                value += user.get('username');
            });

            this.groups.forEach(function(group) {
                if (value.length) {
                    value += ', ';
                }

                value += group.get('name');
            });

            this.$('.share-with').val(value);
        }
    });

    return ShareView;
});