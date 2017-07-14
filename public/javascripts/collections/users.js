define([
    'underscore',
    'backbone',
    'models/user'
], function (_, Backbone, User) {
    var Users = Backbone.Collection.extend({
        model: User,

        url : jsRoutes.controllers.Users.manage().url
    });

    return Users;
});