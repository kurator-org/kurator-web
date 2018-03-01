define([
    'jquery',
    'underscore',
    'backbone',
    'app',
    'collections/workflows',
    'text!templates/workflow.html'
], function ($, _, Backbone, app, WorkflowsCollection, workflowTpl) {
    var WorkflowsView = Backbone.View.extend({
        template: _.template(workflowTpl),

        events: {
            'change .filter-input-btn': 'filterByInput',
            'change .filter-class-btn': 'filterByClass'
        },

        initialize: function () {
            this.filter = {
                'search': null,
                'input': 'any',
                'dwcclass': 'any'
            },

            this.fetchWorkflows();
        },

        render: function () {
            console.log(this.collection.toJSON());
            this.$el.html(this.template({definitions : this.collection.toJSON(), infoImg : app.assetsUrl + "images/info.png"}));

            // get the dom element that matches the values of input and dwcclass currently set as the filter
            var $inputToggle = $('.filter-input-btn[value=' + this.filter['input'] + ']');
            var $classToggle = $('.filter-class-btn[value=' + this.filter['dwcclass'] + ']');

            $inputToggle.parent().addClass('active');
            $inputToggle.prop('checked', true);

            $classToggle.parent().addClass('active');
            $classToggle.prop('checked', true);

            return this;
        },

        filterByInput: function (event) {
            this.filter['input'] = $(event.target).val();
            this.fetchWorkflows();
        },

        filterByClass: function (event) {
            this.filter['dwcclass'] = $(event.target).val();
            this.fetchWorkflows();
        },

        fetchWorkflows: function () {
            console.log(this.filter);

            this.collection = new WorkflowsCollection(this.filter['search'], this.filter['input'], this.filter['dwcclass']);
            this.listenTo(this.collection, 'update', this.render);

            this.collection.fetch();
        }
    });

    return WorkflowsView;
});