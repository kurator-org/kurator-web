define([
    'app'
], function (app) {
    var WebRouter = Backbone.Router.extend({
        currentView : null,

        routes: {
            "run": 'workflow',
            "workflow/:name": "run",
            "status": 'status',
            "users": 'users',
            "runs/:uid": "runs",
            "deploy": 'deploy',
            "report/:runId": 'report',
            "dataset/:runId": 'dataset'
            //"*actions": "defaultRoute"
        },

        navigateToView: function(view) {
            var current = this.currentView;
            if (current) {
                if (current.onBeforeClose)
                    current.onBeforeClose();
                current.remove();
            }

            this.currentView = view;

            console.log("navigate to view");
            // Check auth before rendering current view
            var self = this;
            app.session.checkAuth({
                success: function(res){
                    // If auth successful, render inside the page wrapper
                    $('#container').html(view.render().el);
                }, error: function(res){
                    window.location = jsRoutes.controllers.Users.login().url;
                }
            });
        }
    });

    return WebRouter;
});