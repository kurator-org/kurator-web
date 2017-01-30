define([
    'app'
], function (app) {
    var WebRouter = Backbone.Router.extend({
        routes: {
            "": 'home',
            "login": 'login',
            "workflow/:name": "workflow",
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