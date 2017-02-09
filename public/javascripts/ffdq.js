define([
    'd3'
], function (d3) {

    var FFDQPostProcessor = function (tag, data) {
        this.tag = tag;
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
                            count: this.data.before.assurance
                        }, {
                            stage: 'After',
                            count: this.data.after.assurance
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
                    svg = d3.select(this.tag)
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

            renderDatasetSpreadsheet: function($el) {
                    var json = {
                        headers: ['ID', 'Country', 'Code', 'Currency', 'Level', 'Units', 'Date', 'Change'],
                        rows: [
                            ['1', '', 'EUR', 'Euro', '0.9033', 'EUR / USD', '08/19/2015', '0.26%'],
                            ['2', '', 'JPY', 'Japanese Yen', '124.3870', 'JPY / USD', '08/19/2015', '0.01%'],
                            ['3', '', 'GBP', 'Pound Sterling', '0.6396', 'GBP / USD', '08/19/2015', '0.00%'],
                            ['4', '', 'CHF', 'Swiss Franc', '0.9775', 'CHF / USD', '08/19/2015', '0.08%'],
                            ['5', '', 'CAD', 'Canadian Dollar', '1.3097', 'CAD / USD', '08/19/2015', '-0.05%'],
                            ['6', '', 'AUD', 'Australian Dollar', '1.3589', 'AUD / USD', '08/19/2015', '0.20%'],
                            ['7', '', 'NZD', 'New Zealand Dollar', '1.5218', 'NZD / USD', '08/19/2015', '-0.36%'],
                            ['8', '', 'SEK', 'Swedish Krona', '8.5280', 'SEK / USD', '08/19/2015', '0.16%'],
                            ['9', '', 'NOK', 'Norwegian Krone', '8.2433', 'NOK / USD', '08/19/2015', '0.08%'],
                            ['10', '', 'BRL', 'Brazilian Real', '6.3961', 'BRL / USD', '08/19/2015', '-0.09%'],
                            ['11', '', 'CNY', 'Chinese Yuan', '6.3961', 'CNY / USD', '08/19/2015', '0.04%'],
                            ['12', '', 'RUB', 'Russian Rouble', '65.5980', 'RUB / USD', '08/19/2015', '0.59%'],
                            ['13', '', 'INR', 'Indian Rupee', '65.3724', 'INR / USD', '08/19/2015', '0.26%'],
                            ['14', '', 'TRY', 'New Turkish Lira', '2.8689', 'TRY / USD', '08/19/2015', '0.92%'],
                            ['15', '', 'THB', 'Thai Baht', '35.5029', 'THB / USD', '08/19/2015', '0.44%'],
                            ['16', '', 'IDR', 'Indonesian Rupiah', '13.8300', 'IDR / USD', '08/19/2015', '-0.09%'],
                            ['17', '', 'MYR', 'Malaysian Ringgit', '4.0949', 'MYR / USD', '08/19/2015', '0.10%'],
                            ['18', '', 'MXN', 'Mexican New Peso', '16.4309', 'MXN / USD', '08/19/2015', '0.17%'],
                            ['19', '', 'ARS', 'Argentinian Peso', '9.2534', 'ARS / USD', '08/19/2015', '0.11%']
                        ]
                    };

                    var table = $('<table></table>');

                    // Create table header
                    var thead = $('<thead></thead>');
                    var tr = $('<tr></tr>');
                    thead.append(tr);

                    json.headers.forEach(function(header, index) {
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

                    json.rows.forEach(function(rowData, index) {
                        var row = $('<tr></tr>');
                        tbody.append(row);

                        row.append('<th>' + Number(1+index) + '</th>');
                        rowData.forEach(function(cellData) {
                            row.append($('<td>' + cellData + '</td>'));
                        });
                    });

                    table.append(tbody);

                    // Apend table to view
                    $el.find('.spreadsheet-view').append(table);

                    // Create fixed table headers
                    var target = $('.spreadsheet-view table thead');
                    var clone = target.clone();

                    clone.css('position', 'fixed');

                    clone.find('th').width(function(i,val) {
                        var headers = target.find('th');
                        return headers.eq(i).width();
                    });

                    // Overlay clone over original headers
                    var height = clone.find('th:first-child').outerHeight();
                    clone.css('margin-top', -height);
                    $el.find('table').prepend(clone);
            }
        };

    return FFDQPostProcessor;
});