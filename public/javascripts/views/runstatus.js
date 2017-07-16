define([
    'jquery',
    'underscore',
    'backbone',
    'collections/runs',
    'views/runs',
    'views/sharedruns',
    'views/sharerun',
    'text!templates/status.html'
], function ($, _, Backbone, Runs, RunsView, SharedRunsView, ShareView, statusTpl) {

    var RunStatusView = Backbone.View.extend({
        template: _.template(statusTpl),

        events: {
            'click .remove-btn': 'removeRuns',
            'click .share-btn': 'shareRuns',
            'shown.bs.tab a[data-toggle="tab"]': 'toggleTab'
        },

        initialize: function (options) {
            this.activeTab = '#user-runs';
            this.options = options;

            //this.listenTo(this.runsView, 'selectionChange', this.render);
        },

        render: function () {
            this.$el.html(this.template({}));

            this.runs = new Runs({ 'uid': this.options.uid });
            this.runsView = new RunsView({ collection: this.runs, el: this.$('#user-runs') });
            this.listenTo(this.runsView, 'selectionChange', this.renderControls);

            this.sharedView = new SharedRunsView({ collection: this.runs, el: this.$('#shared-runs') });

            return this;
        },

        renderControls: function (selected) {
            this.$('#run-controls button').prop('disabled', !selected.length);
        },

        removeRuns: function () {
            this.runsView.removeSelected();
        },

        toggleTab: function (evt) {
            this.activeTab = $(evt.target).attr('href');
        },

        shareRuns: function (evt) {
            var view = new ShareView({model: this.runs.get(1)});
            $('#dialog').html(view.$el);
        }
    });

    return RunStatusView;
});