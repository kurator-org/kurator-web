define([
    'jquery',
    'underscore',
    'backbone',
    'ffdq',
    'text!templates/reports.html'
], function ($, _, Backbone, FFDQPostProcessor, reportsTpl) {
    var ReportsView = Backbone.View.extend({
        template: _.template(reportsTpl),

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
                        var container = that.$el.find('#chart-' + index);
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

    return ReportsView;
});