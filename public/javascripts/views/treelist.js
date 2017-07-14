define([
    'jquery',
    'underscore',
    'backbone',
    'text!templates/treelist.html'
], function ($, _, Backbone, treeListTpl) {

    var TreeListView = Backbone.View.extend({
        template: _.template(treeListTpl),

        events: {
            'mouseup .user-group': 'mouseUpNode'
        },

        initialize: function () {
            this.listenTo(this.collection, 'sync', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({ groups: this.collection.toJSON() }));

            var that = this;
            this.$tree = this.$('#tree-view').jstree().on('changed.jstree', function (e, data) {
                console.log(data.selected[0]);
            });

            return this;
        },

        mouseUpNode: function (node) {
            if (this.dragUser) {
                this.targetGroup = node.currentTarget;
            }
            console.log(this);
        },

        selectNode: function (node) {
            this.collection.fetch();
        },

        draggingUser: function (model) {
            this.dragUser = model;

            this.$('#tree-view li').each(function(elem) {
                if (!$(this).hasClass('user-group')) {
                    $("#tree-view").jstree().disable_node(this.id);
                }
            });

            console.log(this);
        },

        droppedUser: function (model) {
            // if mouseover a group instead of empty space
            if (this.targetGroup) {
                var group = $(this.targetGroup).attr('value');
                var user = model.get('id');

                console.log('add user: ' + user + ' to group: ' + group);
                this.trigger('usermoved', { user: user, group: group });

                delete this.targetGroup;
            }

            // mouseover empty space, cancel the move
            delete this.dragUser;

            this.$('#tree-view li').each(function() {
                if (!$(this).hasClass('user-group')) {
                    $("#tree-view").jstree().enable_node(this.id);
                }
            });
        },

        eventsHandler: function (e) {
            console.log(e);
        }
    });

    return TreeListView;
});