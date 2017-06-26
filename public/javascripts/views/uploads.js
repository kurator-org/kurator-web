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
            'submit #upload-file': 'uploadFile',
            'change #upload': 'fileSelected'
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
            var progressBar = this.$('.progress-bar');
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
                cache: false,
                contentType: false,
                processData: false,
                xhr: function() {
                    var xhr = new window.XMLHttpRequest();
                    //Upload progress
                    xhr.upload.addEventListener("progress", function(evt){
                        console.log("test");
                        if (evt.lengthComputable) {
                            var progress = evt.loaded / evt.total;
                            progressBar.css('width', (progress*100)+'%');
                        }
                    }, false);

                    return xhr;
                }
            }).done(function (data) {
                    that.collection.fetch();

                    spinner.hide();
            });


            console.log(event);

            return this;
        },

        fileSelected: function (event) {
            if (event.target.length != 0 ) {
                this.$('.upload-btn').removeClass('disabled');
            }
        }
    });

    return UploadsView;
});