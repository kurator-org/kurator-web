define([
    'app'
], function (app) {
    var WebRouter = Backbone.Router.extend({
        currentView : null,

        routes: {
            "run": 'workflow',
            "login": 'login',
            "workflow/:name": "run",
            "status": 'status',
            "users": 'users',
            "register": 'register',
            "deploy": 'deploy'
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
            $('#container').html(view.render().el);
        }
    });

    return WebRouter;
});