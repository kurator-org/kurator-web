define([
    'jquery',
    'underscore',
    'backbone',
    'models/upload',
], function ($, _, Backbone, UploadModel) {

    var FileSelectView = Backbone.View.extend({

        events: {
            'click .toggle-btn': 'toggleSelect'
        },

        initialize: function (options) {
            this.fieldName = options.fieldName;
            this.toggleText = 'Click here to select a file instead.';

            // Create the file upload element
            this.$file = $('<input>')
                .attr('type', 'file')
                .attr('name', this.fieldName);

            // Create the file selection element
            this.$select = $('<select>')
                .attr('name', this.fieldName);

            this.$input = this.$file; // default element is file upload

            this.listenTo(this.collection, 'update', this.updateSelect);
            //this.listenTo(this.collection, 'update', this.render);

            this.render();
            this.collection.fetch();
        },

        render: function () {
            this.$el.html('');

            this.$el.append('<p><a class="toggle-btn">' + this.toggleText + '</a></p>');
            this.$el.append(this.$input);

            return this;
        },

        updateSelect: function (evt) {
            // Populate file select from the collection of uploaded files
            this.$select.html('');

            this.collection.each(function (upload) {
                console.log(this.$select);

                var option = $('<option>')
                    .attr('value', upload.id)
                    .html(upload.get('filename'));

                this.$select.append(option);
            }, this);
        },

        toggleSelect: function (evt) {
            if (this.$input.is('input')) {
                this.$input = this.$select;
                this.toggleText = 'Click here to upload a new file instead.';
            } else {
                this.$input = this.$file;
                this.toggleText = 'Click here to select a file instead.';
            }

            console.log("hello");
            this.render();
        }

    });

    return FileSelectView;
});