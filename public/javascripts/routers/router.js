define([
    'app',
    'views/hello'
], function (app, HelloView) {

    var AppRouter = Backbone.Router.extend({
        routes: {
            "*actions": "defaultRoute",
        },

        defaultRoute: function(actions) {
            console.log(actions);

            new HelloView();
        }
    });

    return AppRouter;
});