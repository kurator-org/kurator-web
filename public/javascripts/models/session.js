define([
    "app",
], function(app){

    var SessionModel = Backbone.Model.extend({

        defaults: {
            logged_in: false,
            user_id: ''
        },

        url: function(){
            return jsRoutes.controllers.Application.auth().url;
        },

        checkAuth: function(callback) {
            var self = this;

            this.fetch({
                success: function(model, response){
                    console.log(response.user);
                    callback();
                },

                error: function(model, response){
                    console.log(response);
                }
            });
        },

    });

    return SessionModel;
});