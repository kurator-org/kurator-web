define([
    'app',
    'models/upload'
], function (app, Upload) {
    var Uploads = Backbone.Collection.extend({
        model: Upload
    });

    return Uploads;
});