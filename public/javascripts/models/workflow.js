define([
    "app",
], function(app){

    var WorkflowModel = Backbone.Model.extend({

        initialize: function () {
            this.fetch({

                success: function(model, response){
                    console.log(response);
                },

                error: function(model, response){
                    console.log(response);
                }

            });
        },

        url: function(){
            return jsRoutes.controllers.Application.data().url;
        }

    });

    return WorkflowModel;
});