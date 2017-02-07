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
    'text!templates/report.html'
], function (app, WebRouter, SessionModel, workflowTpl, runWorkflowTpl, loginTpl, statusTpl, usersTpl, registerTpl,
             deployTpl, reportTpl) {

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
            $('#run-modal-title').html(this.model.get('title'));
           $('#run-modal-body').html(this.template(this.model.toJSON()));

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

            $('#run-modal').on('hidden.bs.modal', function (e) {
                app.router.navigate("#", {trigger: true});
            });

            $('#run-btn').on('click', function (e) {

                $('#run-modal').on('hidden.bs.modal', function (e) {
                    app.router.navigate("status", {trigger: true});
                });

                $('.progress').show();
                $('#run-workflow').submit();
                $('#run-modal').modal('toggle');

            });

            $('#run-modal').modal({show: true});

            //this.$el.html(this.template(this.model.toJSON()));
        }
    });

    var WorkflowRuns = Backbone.Collection.extend({
        url : function() {
            console.log("userid:" + this.uid);
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

    var ReportSummary = Backbone.Model.extend({
        url : function() {
            return jsRoutes.controllers.Workflows.report(this.runId).url;
        }
    });

    var Users = Backbone.Collection.extend({
        url : jsRoutes.controllers.Users.manage().url,

        modelId: function(attrs) {
            return attrs.username;
        }
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

            var postManageUsers = function(user, active, role, callback) {
                $.ajax({
                    type: "POST",
                    //the url where you want to sent the userName and password to
                    url: jsRoutes.controllers.Users.manageUsers().url,
                    dataType: 'json',
                    async: false,
                    contentType: 'application/json',
                    //json object to sent to the authentication url
                    data: JSON.stringify({username: user.get('username'), active: active, role: role}),
                    success: callback
                });
            }

                var that = this;

            $('.dropdown-menu li').on('click', function (event) {
                var username = $(this).parent().attr('id').substr(5);
                var user = that.collection.get(username);

                var role = $(this).text();

                postManageUsers(user, user.get('active'), role, function (data) {
                    user.set('role', data.role);

                    $('#role_value_' + data.username).html('<b>' + data.role + '</b>');

                    console.log(data);
                });
            });

            $('.status-btn').on('click', function (event) {
                var username = $(this).attr('id').substr(7);
                var user = that.collection.get(username);

                var active = user.get('active') ? false : true;

                postManageUsers(user, active, user.get('role'), function (data) {
                    user.set('active', data.active);

                    if (data.active) {
                        $('#status_' + data.username)
                            .removeClass('btn-warning')
                            .addClass('btn-success')
                            .html('<b>Active</b>');
                    } else {
                        $('#status_' + data.username)
                            .removeClass('btn-success')
                            .addClass('btn-warning')
                            .html('<b>Inactive</b>');
                    }

                    console.log(data);
                });
            });

            // $('#create-workshop-form').submit(function (event) {
            //     event.preventDefault();
            //     console.log("submitting form...");
            //
            //     //grab all form data
            //     var formData = new FormData($(this)[0]);
            //
            //     $.ajax({
            //         url: $(this).attr('action'),
            //         type: 'POST',
            //         data: formData,
            //         async: false,
            //         cache: false,
            //         contentType: false,
            //         processData: false,
            //         success: function (data) {
            //             console.log(data);
            //         }
            //     });
            //
            //     return false;
            // });
            //
            $('#create-btn').click(function (event) {
                $('#create-workshop-form').submit();
             });

            return this;
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

            return this;
        }
    });

    var ReportView = Backbone.View.extend({
        template: _.template(reportTpl),

        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
            this.model.fetch();
        },

        render: function () {
            console.log(this.model.toJSON());
            this.$el.html(this.model.toJSON());

            return this;
        }
    });

    app.router.on("route:status", function () {
        $(".nav-pills li").removeClass("active");
        $('.status-pill').addClass('active');

        var runs =  new WorkflowRuns();

        if (app.view_as) {
            runs.uid = app.view_as;
        } else {
            runs.uid = window.uid;
        }

        var statusView = new WorkflowRunsView({collection: runs });
        this.navigateToView(statusView);
    });

    app.router.on("route:users", function () {
        var usersView = new UserManagementView({collection: new Users()});
        this.navigateToView(usersView);
    });

    app.router.on("route:runs", function (uid) {
        console.log(uid);
        var runs = new WorkflowRuns();
        runs.uid = uid;

        var statusView = new WorkflowRunsView({collection: runs});
        this.navigateToView(statusView);
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

    app.router.on("route:report", function (runId) {
        var report = new ReportSummary();

        console.log("runId: " + runId);
        report.runId = runId;
        var reportView = new ReportView({ model : report});
        this.navigateToView(reportView);
    });

    // Fetch list of workflows
    //var workflows = new Workflows();
    //app.router.navigateToView(new WorkflowsView({ collection: new Workflows() }));

    $('#user-select-btn').click(function (event) {
        var userlist = new Users();
        userlist.fetch({
            success: function (data) {
                var user = data.toJSON().filter(function (value) {
                    return value.username == $('#user-select').val();
                })[0];

                // TODO: make this session variable state that persists between page loads
                app.view_as = user.id;

                $('#view-as-text').html('<span class="label label-warning" style="font-size: .9em">Viewing as <i>' + user.username + '</i></span>');
                $('#page-alert').html('<div class="alert alert-warning alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Viewing workflows page as ' + user.username + '.</strong> Navigating away from the workflows page will restore view state to the currently logged in user.</div>');
                $('#view-as-user').toggle();
                $('#view-as-self').toggle();

                // TODO: reset view differently, this is a hack for now
                if (app.router.currentView instanceof WorkflowRunsView) {
                    var runs =  new WorkflowRuns();

                    if (app.view_as) {
                        runs.uid = app.view_as;
                    }

                    var statusView = new WorkflowRunsView({collection: runs });
                    app.router.navigateToView(statusView);
                }
            }
        });

        $('#user-select-modal').modal('toggle');
    });

    $('#view-as-self').click(function (event) {
        $('#view-as-text').html('');
        $('#page-alert').html('<div class="alert alert-success alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>View state reset to logged in user.</strong> Viewing workflows page as current user.</div>');
        $(this).toggle();
        $('#view-as-user').toggle();

        delete app.view_as;

        // TODO: reset view differently, this is a hack for now
        if (app.router.currentView instanceof WorkflowRunsView) {
            var runs =  new WorkflowRuns();
            runs.uid = window.uid;

            var statusView = new WorkflowRunsView({collection: runs });
            app.router.navigateToView(statusView);
        }
    });

});