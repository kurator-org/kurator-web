define([
    'd3'
], function (d3) {

    var FFDQPostProcessor = function (tag, measure) {
        this.tag = tag;
        this.measure = measure;
    }

    FFDQPostProcessor.prototype = {
        renderBinarySummary: function () {
                var margins = {
                        top: 12,
                        left: 50,
                        right: 24,
                        bottom: 24
                    },
                    width = 320 - margins.left - margins.right,
                    height = 100 - margins.top - margins.bottom,

                    // Construct d3 dataset object from measure

                    dataset = [{
                        data: [{
                            stage: 'Before',
                            count: this.measure.before.assurance
                        }, {
                            stage: 'After',
                            count: this.measure.after.assurance
                        }],
                        name: 'Assurance'
                    }, {
                        data: [{
                            stage: 'Before',
                            count: this.measure.before.complete
                        }, {
                            stage: 'After',
                            count: this.measure.after.complete
                        }],
                        name: 'Complete'
                    }, {
                        data: [{
                            stage: 'Before',
                            count: this.measure.before.incomplete
                        }, {
                            stage: 'After',
                            count: this.measure.after.incomplete
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
            }
        };

    return FFDQPostProcessor;
});