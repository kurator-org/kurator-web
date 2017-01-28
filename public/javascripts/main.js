require.config({

    paths: {
        'jquery'                : 'lib/jquery/jquery-min',
        'underscore'            : 'lib/underscore/underscore-min',
        'backbone'              : 'lib/backbone/backbone-min',
        'text'                  : 'lib/requirejs-text/text'
    },

    // non-AMD lib
    shim: {
        'underscore'            : { exports  : '_' },
        'backbone'              : { deps : ['underscore', 'jquery'], exports : 'Backbone' }
    }

});

require([
    'app',
    'routers/router',
    'models/session'
], function (app, WebRouter, SessionModel) {

    app.router = new WebRouter();
    app.session = new SessionModel({});

    app.session.checkAuth(function() {
        // Start the backbone routing once we have captured a user's auth status
        Backbone.history.start();
    });

});