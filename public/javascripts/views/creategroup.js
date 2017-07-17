define([
    'jquery',
    'underscore',
    'backbone',
    'app',
    'text!templates/creategroup.html'
], function ($, _, Backbone, app, createGroupTpl) {

    var CreateGroupView = Backbone.View.extend({
        template: _.template(createGroupTpl),

        events: {
            'hidden.bs.modal .modal': 'close',
            'click #group-submit-btn': 'createGroupSubmit',
            'submit #create-group-form': 'createGroup'
        },

        initialize: function () {
            this.render();
        },

        render: function () {
            this.$el.html(this.template({}));
            this.$('.modal').modal('show');

            return this;
        },

        createGroupSubmit: function (e) {
            this.$('#create-group-form').submit();
        },

        createGroup: function (e) {
            e.preventDefault();

            var name = this.$('input[name=group-name]').val();
            app.currentGroups.create({ name: name});

            this.$('.modal').modal('hide');
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

    return CreateGroupView;
});