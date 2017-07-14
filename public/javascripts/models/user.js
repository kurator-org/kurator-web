define([
    'underscore',
    'backbone'
], function (_, Backbone) {
    var UserModel = Backbone.Model.extend({

        hasGroup: function (group) {
            // if the groups array in the user model does not
            // already contain the group
            var groups = this.get('groups');

            var result = (groups.filter(function (g) {
                if (g.id == group.id) {
                    return true;
                }
            }).length > 0);

            return result;
        }
    });

    return UserModel;
});