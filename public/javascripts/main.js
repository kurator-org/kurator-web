require.config({

    paths: {
        'jquery'                : 'lib/jquery/jquery-min',
        'jquery-ui'             : 'lib/jquery-ui/jquery-ui.min',
        'underscore'            : 'lib/underscore/underscore-min',
        'backbone'              : 'lib/backbone/backbone-min',
        'bootstrap-tokenfield'  : 'lib/bootstrap-tokenfield/bootstrap-tokenfield.min',
        'jstree'                : 'lib/jstree/jstree.min',
        'paper'                 : 'lib/paper/paper-core',
        'text'                  : 'lib/requirejs-text/text',
        'd3'                    : 'lib/d3/d3.min'
    },

    // non-AMD lib
    shim: {
        'underscore'            : { exports  : '_' },
        'backbone'              : { deps : ['underscore', 'jquery'], exports : 'Backbone' },
        'bootstrap-tokenfield'  : { exports : 'Tokenfield' },
        'paper'                 : { exports: 'paper' },
        'd3'                    : { exports: 'd3' }
    }

});

require([
    'app',
    'router',
    'models/session',
    'views/workflows',
    'views/runworkflow',
    'collections/workflows',
    'collections/groups',
    'collections/runs',
    'collections/reports',
    'collections/records',
    'views/runstatus',
    'views/usermgmt',
    'views/deploy',
    'views/reports',
    'views/records',
    'collections/users',
    'collections/uploads',
    'collections/packages',
    'views/uploads',
    'views/fileselect',
    'text!templates/artifacts.html',
    'text!templates/result.html',
    'text!templates/login.html',
    'text!templates/status.html',
    'text!templates/register.html',
    'text!templates/deploy.html',
    'text!templates/reports.html',
    'text!templates/records.html',
    'bootstrap-tokenfield',
    'jquery-ui'
], function (app, WebRouter, SessionModel, WorkflowsView, RunWorkflowView, Workflows, GroupCollection, Runs, ReportCollection, RecordCollection, RunStatusView, UserManagementView, DeployPackagesView, ReportsView, RecordsView, Users, Uploads, Packages, UploadsView, FileSelectView, artifactsTpl, resultTpl,
             loginTpl, statusTpl, registerTpl, deployTpl, reportTpl, datasetTpl, TokenField, JQueryUI) {

    app.router = new WebRouter();
    app.session = new SessionModel({});

    app.session.checkAuth({

        // Start the backbone routing once we have captured a user's auth status
        complete: function() {
            Backbone.history.start();
        }
    });

    app.assetsUrl = jsRoutes.controllers.Assets.at('').url;

    // Set app globals
    app.workflows = new Workflows();

    app.currentGroups = new GroupCollection();
    app.currentUsers = new Users();

    var viewAsSelf = function () {
        $('#view-as-text').html('');
        $('#page-alert').html('');
        $('#view-as-self').hide();
        $('#view-as-user').show();
    };

    var viewAsUser = function (uid, username) {
        $('#view-as-text').html('<span class="label label-warning" style="font-size: .9em">Viewing as <i>' + username + '</i></span>');
        $('#page-alert').html('<div class="alert alert-warning alert-dismissible" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><strong>Viewing workflows page as ' + username + '.</strong> Navigating away from the workflows page will restore view state to the currently logged in user.</div>');
        $('#view-as-user').hide();
        $('#view-as-self').show();
    };

    app.router.on("route:status", function (uid) {
        $(".nav-pills li").removeClass("active");
        $('.status-pill').addClass('active');

        $('.breadcrumb .active').remove();
        $('.breadcrumb').append('<li class="active"><a href="#status">Status</a></li>');

        // Default is to view workflow runs as currently logged in user

        if (!uid) {
            uid = app.session.get('uid');
        }

        var statusView = new RunStatusView({ uid: uid, onClose: viewAsSelf });
        this.navigateToView(statusView);
    });

    app.router.on("route:users", function () {
        var usersView = new UserManagementView();
        this.navigateToView(usersView);
    });

    app.router.on("route:runs", function (uid) {
        app.currentRuns = new Runs();

        var statusView = new WorkflowRunsView({collection: app.currentRuns});
        this.navigateToView(statusView);
    });

    app.router.on("route:deploy", function () {
        $(".nav-pills li").removeClass("active");
        $('.deploy-pill').addClass('active');

        $('.breadcrumb .active').remove();
        $('.breadcrumb').append('<li class="active"><a href="#deploy">Deploy</a></li>');

        var deployView = new DeployPackagesView({collection: new Packages()});
        this.navigateToView(deployView);
    });

    app.router.on("route:workflow", function () {
        $(".nav-pills li").removeClass("active");
        $('.run-pill').addClass('active');

        $('.breadcrumb .active').remove();
        $('.breadcrumb').append('<li class="active"><a href="#run">Run</a></li>');

        var workflowsView = new WorkflowsView({ collection: app.workflows });
        this.navigateToView(workflowsView);
    });

    app.router.on("route:run", function (name) {
        var runView = new RunWorkflowView({ model : app.workflows.get(name)});
        console.log(app.workflows.get(name));
        runView.render();
        //this.navigateToView(runView);
        //new RunWorkflowView({ model : workflows.get(name)});
    });

    app.router.on("route:report", function (runId) {
        var report = new ReportCollection();

        if (!($('.report-bc').length)) {
            $('.breadcrumb').append('<li class="active report-bc"><a href="#report/' + runId + '">View Report</a></li>');
        }

        report.runId = runId;
        var reportView = new ReportsView({ model : report});
        this.navigateToView(reportView);
    });

    app.router.on("route:upload", function () {
        $(".nav-pills li").removeClass("active");
        $('.upload-pill').addClass('active');

        $('.breadcrumb .active').remove();
        $('.breadcrumb').append('<li class="active"><a href="#upload">Upload</a></li>');

        var uploads = new Uploads();
        uploads.url = jsRoutes.controllers.Users.listUploads(app.session.get('uid')).url;

        var uploadView = new UploadsView({ collection : uploads});
        this.navigateToView(uploadView);
    });

    app.router.on("route:dataset", function (runId) {
        var report = new RecordCollection();
        report.runId = runId;

        var datasetView = new RecordsView({ model : report });
        this.navigateToView(datasetView);
    });

    // Fetch list of workflows
    //var workflows = new Workflows();
    //app.router.navigateToView(new WorkflowsView({ collection: new Workflows() }));

    $('#view-as-user').click(function (event) {
        var model = new Users();

        model.fetch({
            success: function (data) {
                var users = data.toJSON().map(function (user) {
                    return {
                        'id': user.id,
                        'label': user.username,
                        'value': user.username
                    };
                });

                $("#user-select").autocomplete({
                    source: users,
                    select: function( event, ui ) {
                        $(this).val(ui.item.label);

                        $("#user-select-id").val(ui.item.id);
                        return false;
                    }
                });
            }
        });


    });

    $('#user-select-modal').on('shown.bs.modal', function (event) {
        console.log("Modal shown");
    });

    $('#user-select-btn').click(function (event) {
        var username = $('#user-select').val();
        var uid = $('#user-select-id').val();

        app.router.navigate("/status/" + uid, {trigger: true});

        viewAsUser(uid, username);
        /*var userlist = new Users();
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
        });*/

        $('#user-select-modal').modal('toggle');
    });


    $('#view-as-self').click(function (event) {
        app.router.navigate('/status', {trigger: true});
    });

});