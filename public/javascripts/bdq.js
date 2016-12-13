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

    $('#container-eventdate-completeness').highcharts({
        chart: {
            type: 'bar'
        },
        title: {
            text: ''
        },
        tooltip: {
            headerFormat: '{point.x}: ',
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
            reversed: true
        },
        plotOptions: {
            series: {
                stacking: 'normal'
            }
        },
        series: [{
            name: 'Untested',
            data: [5, 5]
        }, {
            name: 'Incomplete',
            data: [2, 1]
        }, {
            name: 'Complete',
            data: [3, 2]
        },
            {
                name: 'Assurance',
                data: [0, 2]
            }],
            credits: {
        enabled: false
    },
    });

  var gaugeOptions = {
      chart: {
          type: 'solidgauge'
      },
      title: "Dataset Measures for Coordinates",
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
      tooltip: {
          enabled: false
      },

      // the value axis
      yAxis: {
          stops: [
              [0.4, '#DF5353'], // red
              [0.9, '#DDDF0D'], // yellow
              [1, '#55BF3B'] // green
          ],
          lineWidth: 0,
          minorTickInterval: null,
          tickPixelInterval: 400,
          tickWidth: 0,
          title: {
              y: -70
          },
          labels: {
              y: 16
          }
      },
      plotOptions: {
          solidgauge: {
              dataLabels: {
                  y: 5,
                  borderWidth: 0,
                  useHTML: true
              }
          }
      }
  };
  // COORDINATES
  // Completeness
  $('#container-coordinates-completeness').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 100,
          title: {
              text: 'Coordinates Completeness'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Completeness',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">%</span></div>'
          },
          tooltip: {
              valueSuffix: ' %'
          }
      }]
  }));
  // Consistency
  $('#container-coordinates-consistency').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 100,
          title: {
              text: 'Coordinates Consistency'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Consistency',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">%</span></div>'
          },
          tooltip: {
              valueSuffix: ' %'
          }
      }]
  }));
  // Precision
  $('#container-coordinates-precision').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 5,
          title: {
              text: 'Coordinates Numerical Precision'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Numerical Precision',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">decimals</span></div>'
          },
          tooltip: {
              valueSuffix: ' decimals'
          }
      }]
  }));
  // SCINAME
  // Completeness
  $('#container-sciName-completeness').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 100,
          title: {
              text: 'SciName Completeness'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Completeness',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">%</span></div>'
          },
          tooltip: {
              valueSuffix: ' %'
          }
      }]
  }));
  // Accuracy
  $('#container-sciName-accuracy').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 100,
          title: {
              text: 'SciName Accuracy'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Accuracy',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">%</span></div>'
          },
          tooltip: {
              valueSuffix: ' %'
          }
      }]
  }));

  // COLLECTED DATE
  // Completeness
  $('#container-collectedDate-completeness').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 100,
          title: {
              text: 'Collected Date Completeness'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Completeness',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">%</span></div>'
          },
          tooltip: {
              valueSuffix: ' %'
          }
      }]
  }));
  // Consistency
  $('#container-collectedDate-consistency').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 100,
          title: {
              text: 'Collected Date Consistency'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Consistency',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">%</span></div>'
          },
          tooltip: {
              valueSuffix: ' %'
          }
      }]
  }));
  // OCCURRENCE
  // Completeness
  $('#container-occurrence-completeness').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 100,
          title: {
              text: 'Occurrence Completeness'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Completeness',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">%</span></div>'
          },
          tooltip: {
              valueSuffix: ' %'
          }
      }]
  }));
  $('#container-occurrence-accuracy').highcharts(Highcharts.merge(gaugeOptions, {
      yAxis: {
          min: 0,
          max: 100,
          title: {
              text: 'Occurrence Accuracy'
          }
      },
      credits: {
          enabled: false
      },
      series: [{
          name: 'Accuracy',
          data: [0],
          dataLabels: {
              format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                  ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">{y:.2f}</span><br/>' +
                     '<span style="font-size:12px;color:silver">%</span></div>'
          },
          tooltip: {
              valueSuffix: ' %'
          }
      }]
  }));

  original();
});
// $( document ).tooltip(
//   {
//     position: { my:"right+15 top", at: "left bottom",collision:'fit' },
//     track: true,
//     content: function(){
//     return $( this ).attr("title");
//     },
//     open: function (event, ui) {
//       ui.tooltip.css("max-width", "800px");
//     }
//   }
// );


function showMessage(text){
$('#msg').html(text);
$('#msg').dialog({width:800,modal: true});
}
/*
* DATASET MEASURES
*/
function measure(url){
$.get(url, function(data, status){
  $('#count').html(data.dataResource.count);

  var info = '<b>'+data.measures.cd4.contextualizedDimension.label+'</b><br><br>';
  info = info + '<b>Description:</b> '+data.measures.cd4.contextualizedDimension.description+'<br><br>';
  info = info + '<b>Specification:</b> '+data.measures.cd4.specification+'<br><br>';
  info = info + '<b>Mechanism:</b> '+data.measures.cd4.mechanism;
  $("#info-coordinates-completeness").html('<a href="javascript:showMessage(\''+info+'\')"><img width=30 src="http://icons.iconarchive.com/icons/ampeross/qetto-2/128/info-icon.png"/>');

  var info = '<b>'+data.measures.cd5.contextualizedDimension.label+'</b><br><br>';
  info = info + '<b>Description:</b> '+data.measures.cd5.contextualizedDimension.description+'<br><br>';
  info = info + '<b>Specification:</b> '+data.measures.cd5.specification+'<br><br>';
  info = info + '<b>Mechanism:</b> '+data.measures.cd5.mechanism;
  $("#info-coordinates-consistency").html('<a href="javascript:showMessage(\''+info+'\')"><img width=30 src="http://icons.iconarchive.com/icons/ampeross/qetto-2/128/info-icon.png"/>');

   var info = '<b>'+data.measures.cd6.contextualizedDimension.label+'</b><br><br>';
   info = info + '<b>Description:</b> '+data.measures.cd6.contextualizedDimension.description+'<br><br>';
   info = info + '<b>Specification:</b> '+data.measures.cd6.specification.replace(/\"/g,'\\\'')+'<br><br>';
   info = info + '<b>Mechanism:</b> '+data.measures.cd6.mechanism;
  $("#info-coordinates-precision").html('<a href="javascript:showMessage(\''+info+'\')"><img width=30 src="http://icons.iconarchive.com/icons/ampeross/qetto-2/128/info-icon.png"/>');

  var info = '<b>'+data.measures.cd9.contextualizedDimension.label+'</b><br><br>';
  info = info + '<b>Description:</b> '+data.measures.cd9.contextualizedDimension.description+'<br><br>';
  info = info + '<b>Specification:</b> '+data.measures.cd9.specification.replace(/\"/g,'\\\'')+'<br><br>';
  info = info + '<b>Mechanism:</b> '+data.measures.cd9.mechanism;
 $("#info-sciName-completeness").html('<a href="javascript:showMessage(\''+info+'\')"><img width=30 src="http://icons.iconarchive.com/icons/ampeross/qetto-2/128/info-icon.png"/>');

 var info = '<b>'+data.measures.cd10.contextualizedDimension.label+'</b><br><br>';
 info = info + '<b>Description:</b> '+data.measures.cd10.contextualizedDimension.description+'<br><br>';
 info = info + '<b>Specification:</b> '+data.measures.cd10.specification.replace(/\"/g,'\\\'')+'<br><br>';
 info = info + '<b>Mechanism:</b> '+data.measures.cd10.mechanism;
$("#info-sciName-accuracy").html('<a href="javascript:showMessage(\''+info+'\')"><img width=30 src="http://icons.iconarchive.com/icons/ampeross/qetto-2/128/info-icon.png"/>');

var info = '<b>'+data.measures.cd13.contextualizedDimension.label+'</b><br><br>';
info = info + '<b>Description:</b> '+data.measures.cd13.contextualizedDimension.description+'<br><br>';
info = info + '<b>Specification:</b> '+data.measures.cd13.specification.replace(/\"/g,'\\\'')+'<br><br>';
info = info + '<b>Mechanism:</b> '+data.measures.cd13.mechanism;
$("#info-collectedDate-completeness").html('<a href="javascript:showMessage(\''+info+'\')"><img width=30 src="http://icons.iconarchive.com/icons/ampeross/qetto-2/128/info-icon.png"/>');

var info = '<b>'+data.measures.cd14.contextualizedDimension.label+'</b><br><br>';
info = info + '<b>Description:</b> '+data.measures.cd14.contextualizedDimension.description+'<br><br>';
info = info + '<b>Specification:</b> '+data.measures.cd14.specification.replace(/\"/g,'\\\'')+'<br><br>';
info = info + '<b>Mechanism:</b> '+data.measures.cd14.mechanism;
$("#info-collectedDate-consistency").html('<a href="javascript:showMessage(\''+info+'\')"><img width=30 src="http://icons.iconarchive.com/icons/ampeross/qetto-2/128/info-icon.png"/>');

var info = '<b>'+data.measures.cd16.contextualizedDimension.label+'</b><br><br>';
info = info + '<b>Description:</b> '+data.measures.cd16.contextualizedDimension.description+'<br><br>';
info = info + '<b>Specification:</b> '+data.measures.cd16.specification.replace(/\"/g,'\\\'')+'<br><br>';
info = info + '<b>Mechanism:</b> '+data.measures.cd16.mechanism;
$("#info-occurrence-completeness").html('<a href="javascript:showMessage(\''+info+'\')"><img width=30 src="http://icons.iconarchive.com/icons/ampeross/qetto-2/128/info-icon.png"/>');

var complied = '<img height=30 src="http://icons.iconarchive.com/icons/graphicloads/colorful-long-shadow/128/Hand-thumbs-up-like-2-icon.png"/>'
var notComplied = '<img height=30 src="http://icons.iconarchive.com/icons/graphicloads/flat-finance/128/dislike-icon.png"/>'

  //COORDINATES
  // Completeness

    var chart = $('#container-eventdate-completeness').highcharts(),
        point,
        assertion;

   /* if (chart) {
        point = chart.series[0].data[0];
        point.update(15.00)
        point = chart.series[0].data[1];
        point.update(30.00)
        point = chart.series[0].data[2];
        point.update(55.00);
    }*/


    $('#container-coordinates-completeness-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc4.assertion=='Complied'?'alert alert-success':'alert alert-danger'));
  $('#container-coordinates-consistency-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc5.assertion=='Complied'?'alert alert-success':'alert alert-danger'));
  $('#container-coordinates-precision-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc6.assertion=='Complied'?'alert alert-success':'alert alert-danger'));

    chart = $('#container-coordinates-completeness').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd4.assertion*100)+"").substring(0,5));
      point.update(assertion);
  }



  // Consistency
  chart = $('#container-coordinates-consistency').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd5.assertion*100)+"").substring(0,5));
      point.update(assertion);
  }
  // Precision
  chart = $('#container-coordinates-precision').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd6.assertion)+"").substring(0,5));
      point.update(assertion);
  }

  //SCINAME
  $('#container-sciName-completeness-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc9.assertion=='Complied'?'alert alert-success':'alert alert-danger'));
  $('#container-sciName-accuracy-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc10.assertion=='Complied'?'alert alert-success':'alert alert-danger'));
  // Completeness
  chart = $('#container-sciName-completeness').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd9.assertion*100)+"").substring(0,5));
      point.update(assertion);
  }
  // Accuracy
  chart = $('#container-sciName-accuracy').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd10.assertion*100)+"").substring(0,5));
      point.update(assertion);
  }

  //COLLECTED DATE
  $('#container-collectedDate-completeness-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc13.assertion=='Complied'?'alert alert-success':'alert alert-danger'));
  $('#container-collectedDate-consistency-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc14.assertion=='Complied'?'alert alert-success':'alert alert-danger'));
  // Completeness
  chart = $('#container-collectedDate-completeness').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd13.assertion*100)+"").substring(0,5));
      point.update(assertion);
  }
  // Consistency
  chart = $('#container-collectedDate-consistency').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd14.assertion*100)+"").substring(0,5));
      point.update(assertion);
  }

  //OCCURRENCE
  $('#container-occurrence-completeness-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc16.assertion=='Complied'?'alert alert-success':'alert alert-danger'));
  $('#container-occurrence-accuracy-validation').removeClass('alert alert-success alert-danger').addClass((data.validations.cc19.assertion=='Complied'?'alert alert-success':'alert alert-danger'));
  // Completeness
  chart = $('#container-occurrence-completeness').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd16.assertion*100)+"").substring(0,5));
      point.update(assertion);
  }
  chart = $('#container-occurrence-accuracy').highcharts();
  if (chart) {
      point = chart.series[0].points[0];
      assertion = Number(((data.measures.cd18.assertion*100)+"").substring(0,5));
      point.update(assertion);
  }
});
}
//var host = "localhost";
var host = "case.bdq.biocomp.org.br";
function original(){
var url = "http://case.bdq.biocomp.org.br:3010/api/v1.0/DQReports/http%3A%2F%2Fcase.biocomp.org.br%3A3010%2Fapi%2Fv1.0%2FOrginalData%2F";
measure(url);
url = "http://case.bdq.biocomp.org.br:3010/api/v1.0/DQReports";
records(url);
}
function control(){
var url = "http://case.bdq.biocomp.org.br:3010/api/v1.0/DQReportControls/http%3A%2F%2Fcase.biocomp.org.br%3A3010%2Fapi%2Fv1.0%2FOrginalData";
measure(url);
url = "http://case.bdq.biocomp.org.br:3010/api/v1.0/DQReportControls";
records(url);
}
function assurance(){
var url = "http://case.bdq.biocomp.org.br:3010/api/v1.0/DQReportAssurances/http%3A%2F%2Fcase.biocomp.org.br%3A3010%2Fapi%2Fv1.0%2FDQReportControls";
measure(url);
url = "http://case.bdq.biocomp.org.br:3010/api/v1.0/DQReportAssurances";
records(url);
}
/*
* RECORDS
*/
function records(url) {
$.get(url+"?filter=%7B%22limit%22%3A%201001%7D", function(data, status){
  var table = [];
  data.forEach(function (item) {
    if(item.dataResource.resourceType!="Dataset"){
      var r = {};
      r.recid = item.dataResource.value.id;
      r.sciName = item.dataResource.value.scientificName?(item.dataResource.value.scientificNameAuthorship ? (item.dataResource.value.scientificName+" "+item.dataResource.value.scientificNameAuthorship):item.dataResource.value.scientificName):"";
      r.collectedDate = item.dataResource.value.eventDate;
      r.coordinates = "("+item.dataResource.value.decimalLatitude+", "+item.dataResource.value.decimalLongitude+")";
      table.push(r)
    }
  })
  if(w2ui['grid'])
    w2ui['grid'].destroy();
  $('#grid').w2grid({
      name: 'grid',
      header: 'Records',
      show: {
          toolbar: true,
          footer: true
      },
      columns: [
          { field: 'recid', caption: 'ID', size: '50px', sortable: true, attr: 'align=center' },
          { field: 'sciName', caption: 'SciName', size: '30%', sortable: true, resizable: true },
          { field: 'collectedDate', caption: 'Collected Date', size: '30%', sortable: true, resizable: true },
          { field: 'coordinates', caption: 'Coordinates', size: '40%', resizable: true }
      ],
      searches: [
          { field: 'sciName', caption: 'Scientific Name', type: 'text' },
          { field: 'coordinates', caption: 'Event Date', type: 'text' }
      ],
      sortData: [{ field: 'sciName', direction: 'ASC'}],
      records: table,
      onSelect: function(event){
        event.onComplete = function () {
          $.get(url+"/"+this.getSelection(), function(data, status){
            $("#record-title").html('<H2>Record:</H2>')
            $("#id").html("<b>id: </b>"+(data.id));
            $("#eventDate").html("<b>eventDate: </b>"+(data.dataResource.value.eventDate));
            $("#oaiid").html("<b>oaiid: </b>"+(data.dataResource.value.oaiid));
            $("#georeferenceSources").html("<b>georeferenceSources: </b>"+(data.dataResource.value.georeferenceSources));
            $("#identifiedBy").html("<b>identifiedBy: </b>"+(data.dataResource.value.identifiedBy));
            $("#geodeticDatum").html("<b>geodeticDatum: </b>"+(data.dataResource.value.geodeticDatum));
            $("#family").html("<b>family: </b>"+(data.dataResource.value.family));
            $("#catalogNumber").html("<b>catalogNumber: </b>"+(data.dataResource.value.catalogNumber));
            $("#recordedBy").html("<b>recordedBy: </b>"+(data.dataResource.value.recordedBy));
            $("#stateProvince").html("<b>stateProvince: </b>"+(data.dataResource.value.stateProvince));
            $("#year").html("<b>year: </b>"+(data.dataResource.value.year));
            $("#dateIdentified").html("<b>dateIdentified: </b>"+(data.dataResource.value.dateIdentified));
            $("#scientificName").html("<b>scientificName: </b>"+(data.dataResource.value.scientificName));
            $("#georeferenceVerificationStatus").html("<b>georeferenceVerificationStatus: </b>"+(data.dataResource.value.georeferenceVerificationStatus));
            $("#scientificNameAuthorship").html("<b>scientificNameAuthorship: </b>"+(data.dataResource.value.scientificNameAuthorship));
            $("#ownerInstitutionCode").html("<b>ownerInstitutionCode: </b>"+(data.dataResource.value.ownerInstitutionCode));
            $("#taxonID").html("<b>taxonID: </b>"+(data.dataResource.value.taxonID));
            $("#collectionCode").html("<b>collectionCode: </b>"+(data.dataResource.value.collectionCode));
            $("#modified").html("<b>modified: </b>"+(data.dataResource.value.modified));
            $("#country").html("<b>country: </b>"+(data.dataResource.value.country));
            $("#basisOfRecord").html("<b>basisOfRecord: </b>"+(data.dataResource.value.basisOfRecord));
            $("#decimalLatitude").html("<b>decimalLatitude: </b>"+(data.dataResource.value.decimalLatitude));
            $("#institutionCode").html("<b>institutionCode: </b>"+(data.dataResource.value.institutionCode));
            $("#county").html("<b>county: </b>"+(data.dataResource.value.county));
            $("#month").html("<b>month: </b>"+(data.dataResource.value.month));
            $("#decimalLongitude").html("<b>decimalLongitude: </b>"+(data.dataResource.value.decimalLongitude));
            $("#locality").html("<b>locality: </b>"+(data.dataResource.value.locality));
            $("#georeferencedBy").html("<b>georeferencedBy: </b>"+(data.dataResource.value.georeferencedBy));

            $("#measures").html('<H2>DQ Measures:</H2>')
            $("#cd1").html(data.measures.cd1.contextualizedDimension.label+":<b> "+data.measures.cd1.assertion+"</b>");
            $("#cd2").html(data.measures.cd2.contextualizedDimension.label+":<b> "+data.measures.cd2.assertion+"</b>");
            $("#cd3").html(data.measures.cd3.contextualizedDimension.label+":<b> "+data.measures.cd3.assertion+"</b>");
            $("#cd11").html(data.measures.cd11.contextualizedDimension.label+":<b> "+data.measures.cd11.assertion+"</b>");
            $("#cd12").html(data.measures.cd12.contextualizedDimension.label+":<b> "+data.measures.cd12.assertion+"</b>");
            $("#cd7").html(data.measures.cd7.contextualizedDimension.label+":<b> "+data.measures.cd7.assertion+"</b>");
            $("#cd8").html(data.measures.cd8.contextualizedDimension.label+":<b> "+data.measures.cd8.assertion+"</b>");
            $("#cd15").html(data.measures.cd15.contextualizedDimension.label+":<b> "+data.measures.cd15.assertion+"</b>");

            $("#validations").html('<H2>DQ Validations:</H2>')
            $("#cc1").html(data.validations.cc1.contextualizedCriterion+'<strong style="color:'+(data.validations.cc1.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc1.assertion+")</strong>");
            $("#cc2").html(data.validations.cc2.contextualizedCriterion+'<strong style="color:'+(data.validations.cc2.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc2.assertion+")</strong>");
            $("#cc3").html(data.validations.cc3.contextualizedCriterion+'<strong style="color:'+(data.validations.cc3.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc3.assertion+")</strong>");
            $("#cc11").html(data.validations.cc11.contextualizedCriterion+'<strong style="color:'+(data.validations.cc11.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc11.assertion+")</strong>");
            $("#cc12").html(data.validations.cc12.contextualizedCriterion+'<strong style="color:'+(data.validations.cc12.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc12.assertion+")</strong>");
            $("#cc17").html(data.validations.cc17.contextualizedCriterion+'<strong style="color:'+(data.validations.cc17.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc17.assertion+")</strong>");
            $("#cc7").html(data.validations.cc7.contextualizedCriterion+'<strong style="color:'+(data.validations.cc7.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc7.assertion+")</strong>");
            $("#cc8").html(data.validations.cc8.contextualizedCriterion+'<strong style="color:'+(data.validations.cc8.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc8.assertion+")</strong>");
            $("#cc15").html(data.validations.cc15.contextualizedCriterion+'<strong style="color:'+(data.validations.cc15.assertion=='Complied'?'green':'red')+'"> ('+data.validations.cc15.assertion+")</strong>");

            if(data.improvements.ce1 || data.improvements.ce2 || data.improvements.ce3){
              $("#improvements").html('<H2>DQ Improvement:</H2>')

              if(data.improvements.ce1)
                $("#ce1").html(data.improvements.ce1.contextualizedEnhancement.label+":<b> scientificName="+data.improvements.ce1.assertion.scientificName+", scientificNameAuthorship="+data.improvements.ce1.assertion.scientificNameAuthorship+"</b>");
              else
                $("#ce1").html('');

              if(data.improvements.ce2)
                $("#ce2").html(data.improvements.ce2.contextualizedEnhancement.label+":<b> eventDate="+data.improvements.ce2.assertion.eventDate+"</b>");
              else
                $("#ce2").html('');

              if(data.improvements.ce3)
                $("#ce3").html(data.improvements.ce3.contextualizedEnhancement.label+":<b> decimalLatitude="+data.improvements.ce3.assertion.decimalLatitude+", decimalLongitude="+data.improvements.ce3.assertion.decimalLongitude+"</b>");
              else
                $("#ce3").html('');
            }else{
              $("#improvements").html('')
              $("#ce1").html('');
              $("#ce2").html('');
              $("#ce3").html('');
            }
          });
        }
      }
  });
});
}
