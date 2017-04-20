define([
    'jquery',
    'underscore',
    'backbone',
    'models/user',
    'text!templates/user.html'
], function ($, _, Backbone, UserModel, userTpl) {

    var UserView = Backbone.View.extend({
        tagName: 'tr',
        template: _.template(userTpl),

        events: {
            'click .status-btn' : 'toggleActive',
            'click .dropdown-menu li a' : 'changeRole'
        },

        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
        },

        render: function () {
            this.$el.html(this.template(this.model.toJSON()));

            this.$('.dropdown-toggle').dropdown();

            if (this.model.get('active')) {
                this.$('.status-btn')
                    .removeClass('btn-warning')
                    .addClass('btn-success')
                    .html('<b>Active</b>');

            } else {
                this.$('.status-btn')
                    .removeClass('btn-success')
                    .addClass('btn-warning')
                    .html('<b>Inactive</b>');
            }

            if (this.model.get('username') == 'admin') {
                this.$('.role-btn').prop('disabled', true);
                this.$('.status-btn').prop('disabled', true);
            }

            return this;
        },

        toggleActive: function(e) {
            var isActive = this.model.get('active');
            this.model.save({ active: !isActive }, {wait: true});
        },

        changeRole: function (e) {
            var selectedRole = $(e.target).text();

            if (selectedRole != this.model.get('role')) {
                this.model.save({ role: selectedRole }, {wait: true});
            }
        }
    });

    return UserView;
});