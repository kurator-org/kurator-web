define([
    'jquery',
    'underscore',
    'backbone',
    'models/upload',
    'text!templates/upload.html'
], function ($, _, Backbone, UploadModel, uploadTpl) {

    var UploadsView = Backbone.View.extend({
        template: _.template(uploadTpl),

        events: {
            'submit #upload-file': 'uploadFile'
        },

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({uploads: this.collection.toJSON()}));
            this.$('.fa-spin').hide();
            this.$('.alert-success').hide();

            return this;
        },
        
        uploadFile: function (event) {
            var spinner = this.$('.fa-spin');
            var uploadBtn = this.$('.upload-btn');
            var alertSuccess = this.$('.alert-success');

            spinner.show();
            uploadBtn.addClass('disabled');

            event.preventDefault();

            //grab all form data
            var formData = new FormData(event.target);

            var that = this;
            $.ajax({
                url: jsRoutes.controllers.Workflows.upload().url,
                type: 'POST',
                data: formData,
                async: false,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    that.collection.fetch();

                    spinner.hide();
                    uploadBtn.removeClass('disabled');
                }
            });


            console.log(event);

            return this;
        }
    });

    return UploadsView;
});