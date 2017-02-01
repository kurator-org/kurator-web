define([
    'app'
], function (app) {
    var WebRouter = Backbone.Router.extend({
        routes: {
            "": 'workflow',
            "login": 'login',
            "workflow/:name": "run",
            "status": 'status',
            "users": 'users',
            "register": 'register',
            "deploy": 'deploy'
            //"*actions": "defaultRoute"
        },

        defaultRoute: function(actions) {
            console.log(actions);
        }
    });

    return WebRouter;
});