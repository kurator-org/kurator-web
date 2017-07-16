define([
    'jquery',
    'underscore',
    'backbone',
    'text!templates/deploy.html'
], function ($, _, Backbone, deployTpl) {
    var DeployPackagesView = Backbone.View.extend({
        template: _.template(deployTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({packages: this.collection.toJSON()}));

            var that = this;
            $('.delete-btn').click(function (event) {
                var package = $(this).attr('id').substr(7);

                $.ajax({
                    type: "POST",
                    //the url where you want to sent the userName and password to
                    url: jsRoutes.controllers.Workflows.deletePackage(package).url,
                    dataType: 'json',
                    async: false,
                    contentType: 'application/json',
                    //json object to sent to the authentication url
                    data: JSON.stringify({package: package}),
                    success: function (data) {
                        that.collection.fetch();
                    }
                });
            });

            return this;
        }
    });

    return DeployPackagesView
});