require.config({

    paths: {
        'jquery'                : 'lib/jquery/jquery-min',
        'jquery-ui'             : 'lib/jquery-ui/jquery-ui.min',
        'underscore'            : 'lib/underscore/underscore-min',
        'backbone'              : 'lib/backbone/backbone-min',
        'bootstrap-tokenfield'  : 'lib/bootstrap-tokenfield/bootstrap-tokenfield.min',
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
    'ffdq',
    'collections/runs',
    'views/runstatus',
    'views/usermgmt',
    'collections/users',
    'collections/uploads',
    'views/uploads',
    'views/fileselect',
    'text!templates/workflow.html',
    'text!templates/artifacts.html',
    'text!templates/result.html',
    'text!templates/runworkflow.html',
    'text!templates/login.html',
    'text!templates/status.html',
    'text!templates/users.html',
    'text!templates/user.html',
    'text!templates/register.html',
    'text!templates/deploy.html',
    'text!templates/report.html',
    'text!templates/dataset.html',
    'bootstrap-tokenfield',
    'jquery-ui'
], function (app, WebRouter, SessionModel, FFDQPostProcessor, Runs, RunStatusView, UserManagementView, Users, Uploads, UploadsView, FileSelectView, workflowTpl, artifactsTpl, resultTpl,
             runWorkflowTpl, loginTpl, statusTpl, registerTpl, deployTpl, reportTpl, datasetTpl, TokenField, JQueryUI) {

    app.router = new WebRouter();
    app.session = new SessionModel({});

    app.session.checkAuth({

        // Start the backbone routing once we have captured a user's auth status
        complete: function() {
            Backbone.history.start();
        }
    });

    app.assetsUrl = jsRoutes.controllers.Assets.at('').url;

    var Workflows = Backbone.Collection.extend({
        url : jsRoutes.controllers.Workflows.list().url,

        modelId: function(attrs) {
            return attrs.name;
        }
    });

    var WorkflowsView = Backbone.View.extend({
        template: _.template(workflowTpl),

        initialize: function () {
            this.listenTo(this.collection, 'update', this.render);
            this.collection.fetch();
        },

        render: function () {
            this.$el.html(this.template({definitions : this.collection.toJSON(), infoImg : app.assetsUrl + "images/info.png"}));

            return this;
        }
    });

    var RunWorkflowView = Backbone.View.extend({
        el: '#run-modal',
        template: _.template(runWorkflowTpl),

        render: function () {
            //this.model.set('uploads', []); TODO: this was just a test, make a subview for the file select

            //console.log(this.model.toJSON());
            $('#run-modal-title').html(this.model.get('title'));
           $('#run-modal-body').html(this.template(this.model.toJSON()));

           this.$('.fileinput').each(function () {
               var uploads = new Uploads();
               uploads.url = jsRoutes.controllers.Users.listUploads(app.session.get('uid')).url;

               var fileSelectView = new FileSelectView({ fieldName: $(this).attr('id'), collection: uploads});
               $(this).html(fileSelectView.el);
           });

            $('#run-workflow').submit(function (event) {
                event.preventDefault();

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
                    }
                });

                return this;
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

            // TODO: fix autocomplete and add support for file upload selection
            /*$('#inputfile').on("change", function(){

                var myfile = $('#inputfile input')[0].files[0];

                console.log(myfile);

                Papa.parse(myfile, {
                    preview: 1,
                    complete: function(results) {
                        var header = results.data[0];
                        console.log(header);

                        $('#tokenfield').tokenfield({
                            autocomplete: {
                                source: header,
                                delay: 100,
                            },
                            showAutocompleteOnFocus: true
                        });
                    }
                });
            });*/

            //this.$el.html(this.template(this.model.toJSON()));
        }
    });

    var WorkflowRuns = Backbone.Collection.extend({
        url : function() {
            return jsRoutes.controllers.Workflows.status(app.session.get('uid')).url
        },

        comparator: function(a, b) {
            a = new Date(a.attributes.startDate);
            b = new Date(b.attributes.startDate);
            return a > b ? -1 : a < b ? 1 : 0;
        }
    });

    var ReportSummary = Backbone.Collection.extend({
        url : function() {
            return jsRoutes.controllers.Workflows.report(this.runId).url;
        }
    });

    var DatasetSummary = Backbone.Model.extend({
        url : function() {
            return jsRoutes.controllers.Workflows.dataset(this.id).url;
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
            this.$el.html(this.template({packages : this.collection.toJSON()}));

            var that = this;
            $('.delete-btn').click(function (event) {
                var package = $(this).attr('id').substr(7);

                $.ajax({
                    type: "POST",
                    //the url where you want to sent the userName and password to
                    url: jsRoutes.controllers.Workflows.deletePackage(package).url,
                    dataType: 'json',
                    async: false,
                    contentType: 'application/json',
                    //json object to sent to the authentication url
                    data: JSON.stringify({package: package}),
                    success: function(data) {
                        that.collection.fetch();
                    }
                });
            });

            return this;
        }
    });

    var DatasetView = Backbone.View.extend({
        template: _.template(datasetTpl),

        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
            this.model.fetch();
        },

        render: function () {
            this.$el.html(this.template({ runId: this.model.runId }));

            console.log(this.model.toJSON());
            var dqReports = this.model.toJSON();
            if (!dqReports[0]) {
                this.$el.append('<p><i>Report unavailable.</i></p>');
            } else {
                var dqReport = dqReports[0].report;

                var postprocessor = new FFDQPostProcessor();
                postprocessor.renderDatasetSpreadsheet(this.$el, dqReport);
            }
            /*if (this.model.toJSON().dataset) {
                var postprocessor = new FFDQPostProcessor();
                postprocessor.renderDatasetSpreadsheet(this.$el, this.model.toJSON());
            }*/

            $('#dataset-tabs a').click(function (e) {
                e.preventDefault()
                $(this).tab('show')
            })

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
            this.$el.html('');

            // Tooltip div
            this.$el.append('<div id="tooltip" class="hidden"><p><span id="value">100</span></p></div>');

            var dqReport = this.model.toJSON();

            if (!dqReport[0]) {
                this.$el.append('<p><i>Report unavailable.</i></p>');
            } else {
                // Create map of test name to assertion metadata in profile
                var dqProfile = {};
                dqReport[0].profile.forEach(function (item) {
                    dqProfile[item.name] = item;
                });

                var total = dqReport[0].report.length;
                var summary = {};

                dqReport[0].report.forEach(function (item) {
                    item.assertions.forEach(function (assertion, i) {

                        if (assertion.type == "MEASURE") {
                            var measure;

                            // create entry for measure if it doesn't exist
                            if (!summary[assertion.name]) {
                                var profile = dqProfile[assertion.name];

                                measure = {
                                    "id": i,
                                    "title": profile.label,
                                    "specification": profile.specification,
                                    "mechanism": profile.mechanism,

                                    "before": {
                                        //    "assurance": 1,
                                        "complete": 0,
                                        "incomplete": 0
                                    },
                                    "after": {
                                        //    "assurance": 1,
                                        "complete": 0,
                                        "incomplete": 0
                                    },
                                    "total": total
                                };

                                summary[assertion.name] = measure;
                            } else {
                                measure = summary[assertion.name];
                            }

                            // update assertion counts
                            if (assertion.stage == "PRE_ENHANCEMENT") {
                                if (assertion.status == "COMPLETE") {
                                    measure.before.complete++;
                                } else if (assertion.status == "NOT_COMPLETE") {
                                    measure.before.incomplete++;
                                }
                            } else if (assertion.stage == "POST_ENHANCEMENT") {
                                if (assertion.status == "COMPLETE") {
                                    measure.after.complete++;
                                } else if (assertion.status == "NOT_COMPLETE") {
                                    measure.after.incomplete++;
                                }
                            }
                        }

                    });
                });

                var measures = [];
                for (test in summary) {
                    measures.push(summary[test]);
                }

                var panel = this.template({measures: measures, runId: this.model.runId});
                this.$el.append(panel);

                if (measures.length > 0) {
                    var that = this;
                    measures.forEach(function (measure, index) {
                        var chart = $('<div></div>');
                        var postprocessor = new FFDQPostProcessor(chart, measure);
                        //console.log($(chart));
                        var container = that.$el.find('#chart-'+index);
                        postprocessor.renderBinarySummary();
                        console.log(container);
                        container.append(chart);
                    });
                } else {
                    this.$el.append('<p><i>No measure summary available for data quality report.</i></p>');
                }
            }

            $('[data-toggle="popover"]').popover();

            return this;
        }
    });

    app.router.on("route:status", function () {
        $(".nav-pills li").removeClass("active");
        $('.status-pill').addClass('active');

        $('.breadcrumb .active').remove();
        $('.breadcrumb').append('<li class="active"><a href="#status">Status</a></li>');

        //if (app.view_as) {
        //    runs.uid = app.view_as;
        //} else {
            //runs.uid = window.uid;
        //}

        var statusView = new RunStatusView({ uid: app.session.get('uid') });
        this.navigateToView(statusView);
    });

    app.router.on("route:users", function () {
        var usersView = new UserManagementView({collection: new Users()});
        this.navigateToView(usersView);
    });

    app.router.on("route:runs", function (uid) {
        var runs = new WorkflowRuns();
        //runs.uid = uid;

        var statusView = new WorkflowRunsView({collection: runs});
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

        var workflowsView = new WorkflowsView({collection: new Workflows()});
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

        if (!($('.report-bc').length)) {
            $('.breadcrumb').append('<li class="active report-bc"><a href="#report/' + runId + '">View Report</a></li>');
        }

        report.runId = runId;
        var reportView = new ReportView({ model : report});
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
        var report = new ReportSummary();
        report.runId = runId;

        var datasetView = new DatasetView({ model : report });
        this.navigateToView(datasetView);
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
            //runs.uid = window.uid;

            var statusView = new WorkflowRunsView({collection: runs });
            app.router.navigateToView(statusView);
        }
    });

});