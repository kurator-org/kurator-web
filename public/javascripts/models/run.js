define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    var Run = Backbone.Model.extend({
        defaults: {
            selected: false
        }
    });

    return Run;
});