define([
    "app",
], function(app){

    var SessionModel = Backbone.Model.extend({

        defaults: {
            logged_in: false,
            username: '',
            uid: '',
            role: ''
        },

        url: function(){
            return jsRoutes.controllers.Users.checkAuth().url;
        },

        checkAuth: function(callback) {
            var self = this;

            this.fetch({
                success: function(model, response){
                    if (response) {
                        self.set({
                            logged_in: true,
                            username: response.username,
                            uid: response.uid,
                            role: response.role
                        });
                    }

                    if('success' in callback) callback.success(model, response);
                    if('complete' in callback) callback.complete();
                },

                error: function(model, response){
                    console.log(response);
                    if('error' in callback) callback.error(model, response);
                    if('complete' in callback) callback.complete();
                }
            });
        },

    });

    return SessionModel;
});