define([
    'underscore',
    'backbone',
    'models/group'
], function (_, Backbone, GroupModel) {
    var GroupCollection = Backbone.Collection.extend({
        model: GroupModel,
        url: jsRoutes.controllers.Users.listGroups().url
    });

    return GroupCollection;
});