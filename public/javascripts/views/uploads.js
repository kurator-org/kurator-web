define([
    'jquery',
    'underscore',
    'backbone',
    'models/upload',
    'text!templates/upload.html'
], function ($, _, Backbone, UploadModel, uploadTpl) {

    var UploadsView = Backbone.View.extend({
        template: _.template(uploadTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({uploads: this.collection.toJSON()}));

            return this;
        }
    });

    return UploadsView;
});