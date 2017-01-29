require.config({

    paths: {
        'jquery'                : 'lib/jquery/jquery-min',
        'underscore'            : 'lib/underscore/underscore-min',
        'backbone'              : 'lib/backbone/backbone-min',
        'paper'                 : 'lib/paper/paper-core',
        'text'                  : 'lib/requirejs-text/text'
    },

    // non-AMD lib
    shim: {
        'underscore'            : { exports  : '_' },
        'backbone'              : { deps : ['underscore', 'jquery'], exports : 'Backbone' },
        'paper'                 : { exports: 'paper' }
    }

});

require([
    'app',
    'router',
    'models/session',
    'text!templates/workflow.html',
    'text!templates/run.html',
    'text!templates/login.html',
    'text!templates/status.html'
], function (app, WebRouter, SessionModel, workflowTpl, runWorkflowTpl, loginTpl, statusTpl) {

    app.router = new WebRouter();
    app.session = new SessionModel({});

    app.session.checkAuth(function() {
        // Start the backbone routing once we have captured a user's auth status
        Backbone.history.start();
    });

    var Workflows = Backbone.Collection.extend({
        url : jsRoutes.controllers.Workflows.list().url,

        modelId: function(attrs) {
            return attrs.name;
        }
    });

    var WorkflowsView = Backbone.View.extend({
        el: '#container',
        template: _.template(workflowTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({definitions : this.collection.toJSON()}));
        }
    });

    var RunWorkflowView = Backbone.View.extend({
        el: '#container',
        template: _.template(runWorkflowTpl),

        initialize: function() {
            this.render();
        },

        render: function () {
            //console.log(this.model.toJSON());
            this.$el.html(this.template(this.model.toJSON()));
        }
    });

    var WorkflowRuns = Backbone.Collection.extend({
        uid: 0,
        url : function() {
            return jsRoutes.controllers.Workflows.status(this.uid).url
        }
    });

    var WorkflowRunsView = Backbone.View.extend({
        el: '#container',
        template: _.template(statusTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({runs : this.collection.toJSON()}));
        }
    });

    var LoginView = Backbone.View.extend({
        el: '#container',
        template: _.template(loginTpl),

        render: function () {
            this.$el.html(this.template());
        }
    });

    app.router.on("route:login", function () {
        var loginView = new LoginView();
        loginView.render();
    });

    app.router.on("route:status", function () {
        var runView = new WorkflowRunsView({collection: new WorkflowRuns()});
        runView.render();
    });

    app.router.on("route:home", function () {
        var workflowsView = new WorkflowsView({collection: workflows});
        workflowsView.render();
    });

    app.router.on("route:workflow", function (name) {
        new RunWorkflowView({ model : workflows.get(name)});
    });

    // Fetch list of workflows
    var workflows = new Workflows();

});