define([
    'underscore',
    'backbone',
    'models/package'
], function (_, Backbone, Package) {
    var Packages = Backbone.Collection.extend({
        model: Package,
        url: jsRoutes.controllers.Workflows.deploy().url
    });

    return Packages;
});