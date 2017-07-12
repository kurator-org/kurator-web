define([
    'jquery',
    'underscore',
    'backbone',
    'collections/groups',
    'text!templates/sharerun.html'
], function ($, _, Backbone, GroupCollection, shareTpl) {

    var ShareView = Backbone.View.extend({
        template: _.template(shareTpl),

        events: {
            'hidden.bs.modal #share-modal': 'close',
            'click #share-submit-btn': 'shareRun'
        },

        initialize: function () {
            this.collection = new GroupCollection();

            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({ groups: this.collection.toJSON() }));
            this.$('#share-modal').modal('show');

            return this;
        },

        shareRun: function (e) {
            console.log('share run...');
            // TODO: ajax post form data to share the runs
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

    return ShareView;
});