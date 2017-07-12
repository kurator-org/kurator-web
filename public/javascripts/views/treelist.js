define([
    'jquery',
    'underscore',
    'backbone',
    'collections/groups',
    'text!templates/treelist.html'
], function ($, _, Backbone, GroupCollection, treeListTpl) {

    var TreeListView = Backbone.View.extend({
        template: _.template(treeListTpl),

        events: {
            'mouseup .user-group': 'mouseUpNode'
        },

        initialize: function (options) {
            this.collection = new GroupCollection();

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({ groups: this.collection.toJSON() }));

            this.$tree = this.$('#tree-view').jstree();

            return this;
        },

        mouseUpNode: function (node) {
            if (this.dragUser) {
                this.targetGroup = node.currentTarget;
            }
            console.log(this);
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
        }
    });

    return TreeListView;
});