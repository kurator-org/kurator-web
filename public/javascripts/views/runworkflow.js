define([
    'jquery',
    'underscore',
    'backbone',
    'collections/uploads',
    'views/fileselect',
    'app',
    'text!templates/runworkflow.html'
], function ($, _, Backbone, Uploads, FileSelectView, app, runWorkflowTpl) {
    var RunWorkflowView = Backbone.View.extend({
        el: '#run-modal',
        template: _.template(runWorkflowTpl),

        events: {
            'submit #run-workflow': 'runWorkflow'
        },

        render: function () {
            $('#run-modal-title').html(this.model.get('title'));
            $('#run-modal-body').html(this.template(this.model.toJSON()));

            this.$('.fileinput').each(function () {
                var uploads = new Uploads();
                uploads.url = jsRoutes.controllers.Users.listUploads(app.session.get('uid')).url;

                var fileSelectView = new FileSelectView({fieldName: $(this).attr('id'), collection: uploads});
                $(this).html(fileSelectView.el);
            });

            // TODO: refactor these as events in backbone
            $('#run-modal').on('hidden.bs.modal', function (e) {
                app.router.navigate("#", {trigger: true});
            });

            $('#run-btn').on('click', function (e) {

                $('#run-modal').on('hidden.bs.modal', function (e) {
                    app.router.navigate("status", {trigger: true});
                });

                $('.progress').show();
                $('#run-workflow').submit();
                $('#run-modal').modal('toggle');

            });

            $('#run-modal').modal({show: true});

            // TODO: fix autocomplete and add support for file upload selection
            /*$('#inputfile').on("change", function(){

             var myfile = $('#inputfile input')[0].files[0];

             console.log(myfile);

             Papa.parse(myfile, {
             preview: 1,
             complete: function(results) {
             var header = results.data[0];
             console.log(header);

             $('#tokenfield').tokenfield({
             autocomplete: {
             source: header,
             delay: 100,
             },
             showAutocompleteOnFocus: true
             });
             }
             });
             });*/

            //this.$el.html(this.template(this.model.toJSON()));
        },

        runWorkflow: function (event) {
            console.log("submit");

            event.preventDefault();

            //grab all form data
            var formData = new FormData(event.target);

            var name = this.model.get('name');
            console.log(name);

            $.ajax({
                url: jsRoutes.controllers.AsyncController.scheduleRun(name).url,
                type: 'POST',
                data: formData,
                async: false,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {
                    $('.progress').hide();
                }
            });

            return this;
        }
    });

    return RunWorkflowView;
});