define([
    'app',
    'text!templates/hello.html',
], function (app, helloTemplate) {

    var HelloView = Backbone.View.extend({
        el: '#container',

        template: _.template(helloTemplate),

        initialize: function(){
            this.render();
        },

        render: function(){
            this.$el.html(this.template({name: 'World'}));
        }
    });

    return HelloView;
});