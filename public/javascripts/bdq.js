$(document).ready(function() {

    /*$('#container-eventdate-completeness').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: 0,
            plotShadow: false
        },
        title: {
            text: ''
        },
        tooltip: {
            headerFormat: '',
            pointFormat: '<b>{point.percentage:.1f}%</b>'
        },
        pane: {
            center: ['50%', '85%'],
            size: '140%',
            startAngle: -90,
            endAngle: 90,
            background: {
                backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
                innerRadius: '60%',
                outerRadius: '100%',
                shape: 'arc'
            }
        },
        plotOptions: {
            pie: {
                startAngle: -90,
                endAngle: 90,
                center: ['50%', '85%'],
                size: '140%',
            }
        },
        yAxis: {
            min: 0,
            max: 100
        },
        series: [{
            type: 'pie',
            name: 'Completeness',
            innerSize: '50%',
            data: [
                { name: 'Complete', color: '#55BF3B', y: 0 },
                { name: 'Not Complete', color: '#DF5353', y: 0 },
                { name: 'Untested', color: '#EEEEEE', y: 0 }
            ]
        }],
        credits: {
            enabled: false
        },
    });*/

    var measure = {
        "title" : "Event Date Completeness",
        "specification" : "Check that the value of dwc:eventDate is not empty.",
        "mechanism" : "Kurator: DateValidator",

        "before" : {
            "complete" : 2,
            "incomplete" : 10
        },
        "after" : {
            "complete" : 6,
            "incomplete" : 6
        },
        "total" : 12
    };

    $('#container-eventdate-completeness').highcharts({
        chart: {
            type: 'bar'
        },
        title: {
            text: ''
        },
        tooltip: {
            headerFormat: '{series.name}: ',
            pointFormat: '<b>{point.percentage:.1f}%</b>'
        },
        colors: ['#eeeeee', '#DF5353', '#55BF3B', '#2b621e'],
            xAxis: {
        categories: ['Before', 'After'],
            lineWidth: 0,
            minorGridLineWidth: 0,
            lineColor: 'transparent',
            minorTickLength: 0,
            tickLength: 0
    },
        yAxis: {
            min: 0,
                title: {
                text: ''
            },
            gridLineColor: 'transparent',
                labels: {
                enabled: false
            }
        },
        legend: {
            //reversed: true
            enabled: false
        },
        plotOptions: {
            series: {
                stacking: 'normal'
            }
        },
        series: [{
            name: 'Untested',
            data: [0, 0]
        }, {
            name: 'Incomplete',
            data: [0, 0]
        }, {
            name: 'Complete',
            data: [0, 0]
        },
            {
                name: 'Assurance',
                data: [0, 0]
            }],
            credits: {
        enabled: false
    },
    });

var complied = '<img height=30 src="http://icons.iconarchive.com/icons/graphicloads/colorful-long-shadow/128/Hand-thumbs-up-like-2-icon.png"/>'
var notComplied = '<img height=30 src="http://icons.iconarchive.com/icons/graphicloads/flat-finance/128/dislike-icon.png"/>'

  //COORDINATES
  // Completeness

    var chart = $('#container-eventdate-completeness').highcharts(),
        point,
        assertion;

   if (chart) {
       // Completeness before
        point = chart.series[2].data[0];
        point.update(measure.before.complete);

       // Completeness after
        point = chart.series[2].data[1];
        point.update(measure.after.complete)

       // Incomplete before
        point = chart.series[1].data[0];
        point.update(measure.before.incomplete);

       // Incomplete after
       point = chart.series[1].data[1];
       point.update(measure.after.incomplete);
    }

    $('#eventdate-record-count').html(measure.total);

    $('.eventdate-completeness-title').html(measure.title);
    $('#eventdate-completeness-specification').html(measure.specification);
    $('#eventdate-completeness-mechanism').html(measure.mechanism);
});
