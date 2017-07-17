define([
    'underscore',
    'backbone',
    'models/report'
], function (_, Backbone, ReportModel) {
    var ReportCollection = Backbone.Collection.extend({
        model: ReportModel,

        url: function () {
            return jsRoutes.controllers.Workflows.report(this.runId).url;
        }
    });

    return ReportCollection;
});