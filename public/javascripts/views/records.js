define([
    'jquery',
    'underscore',
    'backbone',
    'ffdq',
    'text!templates/records.html'
], function ($, _, Backbone, FFDQPostProcessor, recordsTpl) {
    var RecordsView = Backbone.View.extend({
        template: _.template(recordsTpl),

        initialize: function () {
            this.listenTo(this.model, 'change', this.render);
            this.model.fetch();
        },

        render: function () {
            this.$el.html(this.template({runId: this.model.runId}));

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

    return RecordsView;
});