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
    'text!templates/deploy.html',
    'text!templates/home.html'
], function (app, WebRouter, SessionModel, workflowTpl, runWorkflowTpl, loginTpl, statusTpl, usersTpl, registerTpl,
             deployTpl, homeTpl) {

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
        template: _.template(workflowTpl),

        initialize: function () {
            console.log("initialize");
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            console.log("render");
            this.$el.html(this.template({definitions : this.collection.toJSON()}));

            return this;
        }
    });

    var RunWorkflowView = Backbone.View.extend({
        el: '#run-modal',
        template: _.template(runWorkflowTpl),

        render: function () {
            //console.log(this.model.toJSON());
            $('.modal-title').html(this.model.get('title'));
           $('.modal-body').html(this.template(this.model.toJSON()));

            $('#run-workflow').submit(function (event) {
                event.preventDefault();
                console.log("submitting form...");

                //grab all form data
                var formData = new FormData($(this)[0]);

                $.ajax({
                    url: $(this).attr('action'),
                    type: 'POST',
                    data: formData,
                    async: false,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function (data) {
                        $('.progress').hide();
                        console.log(data.runId);
                    }
                });

                return false;
            });

            $('.modal').on('hidden.bs.modal', function (e) {
                app.router.navigate("#", {trigger: true});
            });

            $('#run-btn').on('click', function (e) {

                $('.modal').on('hidden.bs.modal', function (e) {
                    app.router.navigate("status", {trigger: true});
                });

                $('.progress').show();
                $('#run-workflow').submit();
                $('.modal').modal('toggle');

            });

            $('.modal').modal({show: true});

            //this.$el.html(this.template(this.model.toJSON()));
        }
    });

    var WorkflowRuns = Backbone.Collection.extend({
        uid: 0,
        url : function() {
            return jsRoutes.controllers.Workflows.status(this.uid).url
        },
        comparator: function(a, b) {
            a = new Date(a.attributes.startDate);
            b = new Date(b.attributes.startDate);
            return a > b ? -1 : a < b ? 1 : 0;
        }
    });

    var WorkflowRunsView = Backbone.View.extend({
        template: _.template(statusTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            //this.listenTo(app.router, 'route', this.beforeClose);
            this.collection.fetch();

            var that = this;
            this.timer = setInterval(function() {
                    that.collection.fetch();
                    console.log("fetch...");
                }, 5000);
        },

        render: function () {
            var runs = this.collection.toJSON();
            this.$el.html(this.template({runs : runs }));

            runs.forEach(function(run) {
                var statusEl = $('#'+run.id);

                switch (run.status) {
                    case "RUNNING":
                        $('#status'+run.id).html($('<span class="label label-default" style="font-size: .9em">Running</span>'));
                        break;
                    case "SUCCESS":
                        $('#status'+run.id).html($('<span class="label label-success" style="font-size: .9em">Complete</span>'));
                        break;
                    case "ERRORS":
                        $('#status'+run.id).html($('<span class="label label-danger" style="font-size: .9em">Errors</span>'));
                        break;
                    default:
                        console.log('status');
                }
            });

            $('[data-toggle="tooltip"]').tooltip();

            return this;
        },

        onBeforeClose: function () {
            console.log("Before close");
            if (this.timer) {
                console.log("has timer: " + this.timer);
                clearInterval(this.timer);
            }
        }
    });

    var Users = Backbone.Collection.extend({
        url : jsRoutes.controllers.Users.manage().url
    });

    var UserManagementView = Backbone.View.extend({
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

    app.router.on("route:status", function () {
        $(".nav-pills li").removeClass("active");
        $('.status-pill').addClass('active');

        var statusView = new WorkflowRunsView({collection: new WorkflowRuns()});
        this.navigateToView(statusView);
    });

    app.router.on("route:users", function () {
        var usersView = new UserManagementView({collection: new Users()});
        this.navigateToView(usersView);
    });

    app.router.on("route:deploy", function () {
        var deployView = new DeployPackagesView({collection: new Packages()});
        this.navigateToView(deployView);
    });

    app.router.on("route:workflow", function () {
        $(".nav-pills li").removeClass("active");
        $('.run-pill').addClass('active');
        var workflowsView = new WorkflowsView({collection: new Workflows()});
        console.log(workflowsView);
        this.navigateToView(workflowsView);
    });

    app.router.on("route:run", function (name) {
        var currentView = app.router.currentView;
        var runView = new RunWorkflowView({ model : currentView.collection.get(name)});
        runView.render();
        //this.navigateToView(runView);
        //new RunWorkflowView({ model : workflows.get(name)});
    });

    // Fetch list of workflows
    //var workflows = new Workflows();
    app.router.navigateToView(new WorkflowsView({ collection: new Workflows() }));

});