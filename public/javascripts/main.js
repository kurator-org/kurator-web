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
    'text!templates/status.html',
    'text!templates/users.html',
    'text!templates/register.html',
    'text!templates/deploy.html'
], function (app, WebRouter, SessionModel, workflowTpl, runWorkflowTpl, loginTpl, statusTpl, usersTpl, registerTpl,
             deployTpl) {

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

    var RegisterView = Backbone.View.extend({
        el: '#container',
        template: _.template(registerTpl),

        render: function () {
            this.$el.html(this.template());
        }
    });

    var Users = Backbone.Collection.extend({
        url : jsRoutes.controllers.Users.manage().url
    });

    var UserManagementView = Backbone.View.extend({
        el: '#container',
        template: _.template(usersTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            console.log(this.collection.models);
            this.$el.html(this.template({users : this.collection.toJSON()}));
        }
    });

    var Packages = Backbone.Collection.extend({
        url : jsRoutes.controllers.Workflows.deploy().url
    });

    var DeployPackagesView = Backbone.View.extend({
        el: '#container',
        template: _.template(deployTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            console.log(this.collection.models);
            this.$el.html(this.template({packages : this.collection.toJSON()}));
        }
    });

    app.router.on("route:login", function () {
        var loginView = new LoginView();
        loginView.render();
    });

    app.router.on("route:register", function () {
        var registerView = new RegisterView();
        registerView.render();
    });

    app.router.on("route:status", function () {
        console.log("test");
        var runView = new WorkflowRunsView({collection: new WorkflowRuns()});
        runView.render();
    });

    app.router.on("route:users", function () {
        var usersView = new UserManagementView({collection: new Users()});
        usersView.render();
    });

    app.router.on("route:deploy", function () {
        var deployView = new DeployPackagesView({collection: new Packages()});
        deployView.render();
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