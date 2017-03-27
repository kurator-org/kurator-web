define([
    'd3'
], function (d3) {

    var FFDQPostProcessor = function (el, data) {
        this.el = el;
        this.data = data;
    }

    FFDQPostProcessor.prototype = {
        renderBinarySummary: function () {
            var margins = {
                    top: 12,
                    left: 50,
                    right: 24,
                    bottom: 30
                },
                width = 320 - margins.left - margins.right,
                height = 100 - margins.top - margins.bottom,

                // Construct d3 dataset object from measure

                dataset = [{
                    data: [{
                        stage: 'Before',
                        //count: this.data.before.assurance
                        count: 0
                    }, {
                        stage: 'After',
                        //count: this.data.after.assurance
                        count: 0
                    }],
                    name: 'Assurance'
                }, {
                    data: [{
                        stage: 'Before',
                        count: this.data.before.complete
                    }, {
                        stage: 'After',
                        count: this.data.after.complete
                    }],
                    name: 'Complete'
                }, {
                    data: [{
                        stage: 'Before',
                        count: this.data.before.incomplete
                    }, {
                        stage: 'After',
                        count: this.data.after.incomplete
                    }],
                    name: 'Incomplete'
                }],

                series = dataset.map(function (d) {
                    return d.name;
                }),

                // Transforms dataset into d3 data
                dataset = dataset.map(function (d) {
                    return d.data.map(function (o, i) {
                        // axis that is used for the the stacked amount is y
                        return {
                            y: o.count,
                            x: o.stage,
                            series: d.name
                        };
                    });
                }),
                stack = d3.layout.stack();

            stack(dataset);

            // Invert the x and y values, and y0 becomes x0
            var dataset = dataset.map(function (group) {
                    return group.map(function (d) {

                        return {
                            x: d.y,
                            y: d.x,
                            x0: d.y0,
                            series: d.series
                        };
                    });
                }),

                // Create the svg
                svg = d3.select($(this.el).get(0))
                    .append('svg')
                    .attr('width', width + margins.left + margins.right)
                    .attr('height', height + margins.top + margins.bottom)
                    .append('g')
                    .attr('transform', 'translate(' + margins.left + ',' + margins.top + ')'),

                xMax = d3.max(dataset, function (group) {
                    return d3.max(group, function (d) {
                        return d.x + d.x0;
                    });
                }),

                xScale = d3.scale.linear()
                    .domain([0, xMax])
                    .range([0, width]),
                months = dataset[0].map(function (d) {
                    return d.y;
                }),

                yScale = d3.scale.ordinal()
                    .domain(months)
                    .rangeRoundBands([0, height], .1),

                xAxis = d3.svg.axis()
                    .scale(xScale)
                    .orient('bottom')
                    .tickValues([0, xMax])
                    .tickFormat(function (x) {
                        return x / xMax * 100 + '%';
                    }),

                yAxis = d3.svg.axis()
                    .scale(yScale)
                    .orient('left'),

                // Green for complete, grey for incomplete
                colors = ["#5cb85c", "#eeeeee"],

                // Create groups
                groups = svg.selectAll('g')
                    .data(dataset)
                    .enter()
                    .append('g'),
                //.style('fill', function (d, i) {
                //    return colors[i];
                //}),

                // Create rectangles and add mouseover even handling for tooltip
                rects = groups.selectAll('rect')
                    .data(function (d) {
                        return d;
                    })
                    .enter()
                    .append('rect')
                    .attr('class', function (d) {
                        return d.series.toLowerCase();
                    })
                    //.attr('style', 'mask: url(#mask-stripe)')
                    .attr('x', function (d) {
                        return xScale(d.x0);
                    })
                    .attr('y', function (d, i) {
                        return yScale(d.y);
                    })
                    .attr('height', function (d) {
                        return yScale.rangeBand();
                    })
                    .attr('width', function (d) {
                        return xScale(d.x);
                    })
                    .on('mouseover', function (d) {
                        var xPos = parseFloat(d3.select(this).attr('x')) / 2 + width / 2;
                        var yPos = parseFloat(d3.select(this).attr('y')) + yScale.rangeBand() / 2;

                        d3.select('#tooltip')
                            .style('left', xPos + 'px')
                            .style('top', yPos + 'px')
                            .select('#value')
                            .text(d.series + ": " + d.x);

                        d3.select('#tooltip').classed('hidden', false);
                    })
                    .on('mouseout', function () {
                        d3.select('#tooltip').classed('hidden', true);
                    });

            svg.append('g')
                .attr('class', 'axis')
                .attr('transform', 'translate(0,' + height + ')')
                .call(xAxis);

            svg.append('g')
                .attr('class', 'axis')
                .call(yAxis);
        },

        renderDatasetSpreadsheet: function ($el, summary) {
            var dataset = summary.dataset;
            console.log(dataset.fields);
            console.log(dataset.records);

            var createTable = function (json) {
                var cellColor = {
                    COMPLIANT: 'green',
                    NOT_COMPLIANT: 'red',
                    FILLED_IN: 'DarkGoldenRod',
                    DATA_PREREQUISITES_NOT_MET: 'grey',
                    EXTERNAL_PREREQUISITES_NOT_MET: 'grey'
                };

                var table = $('<table></table>');

                // Create table header
                var thead = $('<thead></thead>');
                var tr = $('<tr></tr>');
                thead.append(tr);

                json.headers.forEach(function (header, index) {
                    var th = $('<th>' + header + '</th>');

                    // Column for row ids
                    if (index == 0) {
                        idCol = $('<th>&nbsp;</th>').css('height', '25px');
                        tr.append(idCol);
                    }

                    tr.append(th);
                });

                table.append(thead);

                // Create table body
                var tbody = $('<tbody></tbody>');

                json.rows.forEach(function (rowData, index) {
                    var row = $('<tr></tr>');
                    tbody.append(row);

                    row.append('<th>' + Number(1 + index) + '</th>');
                    rowData.forEach(function (cellData) {
                        var cell = $('<td>' + cellData.value + '</td>');

                        if (cellData.status) {
                            cell.css("background-color", cellColor[cellData.status]);
                            cell.css("color", "white");
                        }
                        row.append(cell);

                    });
                });

                table.append(tbody);

                return table;
            };

            var initialValues = {
                headers: [],
                rows: []
            };

            var finalValues = {
                headers: [],
                rows: []
            };

            for (var field in dataset.fields) {
                finalValues.headers.push(field);
                initialValues.headers.push(field);
            }

            var records = dataset.records;
            records.forEach(function (record) {
                var finalValsRow = [];
                var initialValsRow = [];

                for (var field in dataset.fields) {
                    if (field == 'recordId') {
                        finalValsRow.push({ value: record.recordId });
                        initialValsRow.push({ value: record.recordId });
                    } else {
                        var finalVal = record.finalValues[field];

                        if (finalVal) {
                            finalValsRow.push({ value: finalVal });
                        } else {
                            finalValsRow.push({ value: '&nbsp;'});
                        }

                        var initialVal = record.initialValues[field];

                        if (initialVal) {
                            initialValsRow.push({ value: initialVal });
                        } else {
                            initialValsRow.push({ value: '&nbsp;' });
                        }
                    }
                }
                finalValues.rows.push(finalValsRow);
                initialValues.rows.push(initialValsRow);
            });

            // Apend table to view
            $el.find('#initial-values .spreadsheet-view').append(createTable(initialValues));
            $el.find('#final-values .spreadsheet-view').append(createTable(finalValues));

            var assertionsTable = function (div, assertions) {
                // render validations
                var json = {
                    headers: [],
                    rows: []
                };

                // header
                json.headers.push('assertion');

                for (var field in dataset.fields) {
                    json.headers.push(field);
                }

                json.headers.push('comment');
                json.headers.push('status');

                assertions.forEach(function (assertion) {
                    var recordId = assertion.recordId;

                    assertion.assertionRows.forEach(function (assertionRow) {
                        var row = [];
                        row.push({ value: assertionRow.label });
                        row.push({ value: recordId });

                        assertionRow.record.forEach(function (item) {
                            var colNum = json.headers.indexOf(item.field);

                            if (colNum) {
                                row[colNum] = { value: item.value ? item.value : '&nbsp;', status: item.status };
                            }

                            //console.log( + item.field + ": " + item.value + " - " + item.status);
                        });

                        row.push({ value: assertionRow.comment });
                        row.push({ value: assertionRow.status, status: assertionRow.status});

                        json.rows.push(row);
                    })
                });

                div.append(createTable(json));
            };

            assertionsTable($el.find('#validations .spreadsheet-view'), summary.validations);
            assertionsTable($el.find('#improvements .spreadsheet-view'), summary.improvements);

            // Create fixed table headers
            // var target = $('.spreadsheet-view table thead');
            // var clone = target.clone();

            // clone.css('position', 'fixed');

            // clone.find('th').width(function(i,val) {
            //     var headers = target.find('th');
            //     return headers.eq(i).width();
            // });

            // Overlay clone over original headers
            // var height = clone.find('th:first-child').outerHeight();
            // clone.css('margin-top', -height);
            // $el.find('table').prepend(clone);
        }


    };

    return FFDQPostProcessor;
});