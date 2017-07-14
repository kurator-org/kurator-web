define([
    'jquery',
    'underscore',
    'backbone',
    'app',
    'text!templates/createuser.html'
], function ($, _, Backbone, app, createUserTpl) {

    var CreateUserView = Backbone.View.extend({
        template: _.template(createUserTpl),

        events: {
            'hidden.bs.modal .modal': 'close',
            'click #user-submit-btn': 'createUserSubmit',
            'submit #create-user-form': 'createUser'
        },

        initialize: function () {
            this.render();
        },

        render: function () {
            this.$el.html(this.template({ groups: app.currentGroups.toJSON() }));
            this.$('.modal').modal('show');

            return this;
        },

        createUserSubmit: function (e) {
            this.$('#create-user-form').submit();
        },

        createUser: function (e) {
            e.preventDefault();

            // Check validity of the form, only one input so
            // this is assuming that email is invalid for now
            if(this.$('#create-user-form')[0].checkValidity()) {
                this.$('#email')
                    .parent()
                    .removeClass('has-error');

                this.$('.errors').hide();

                var user = { };

                user.email = this.$('input[name=email]').val();
                user.groups = [];

                var groupId = this.$('#group-select :selected').val();
                if (groupId != -1) {
                    user.groups.push(app.currentGroups.get(groupId).toJSON());
                }

                app.currentUsers.create(user);

            } else {
                this.$('#email')
                    .parent()
                    .addClass('has-error');

                this.$('.errors')
                    .html('Oops, please enter a valid email and click "Create User" to continue')
                    .show();

                console.log("invalid form");
            }

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