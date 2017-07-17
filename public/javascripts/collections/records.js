define([
    'underscore',
    'backbone',
    'models/record'
], function (_, Backbone, RecordModel) {
    var RecordCollection = Backbone.Model.extend({
        model: RecordModel,
        url: function () {
            return jsRoutes.controllers.Workflows.dataset(this.id).url;
        }
    });

    return RecordCollection;
});