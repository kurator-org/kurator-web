define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    var ResultModel = Backbone.Model.extend({
        url: function(){
            return jsRoutes.controllers.Workflows.resultArtifacts(this.id).url
        }
    });

    return ResultModel;
});