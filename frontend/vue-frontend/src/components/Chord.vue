<template>
  <div>
    <h1>Chord Diagram</h1>
    <div id="chord-container"></div>
  </div>
</template>

<script>
import * as d3 from "d3";
export default {
  data() {
    return {
      width: 1000,
      height: 1000,
      outerRadius: Math.min(width, height) * 0.5 - 150,
      innerRadius: outerRadius - 10,
      colorScale: {},
      matrix: [],
      nodes: [],
    };
  },

  methods: {
    ribbon: function () {
      d3.ribbonArrow()
        .radius(innerRadius - 1)
        .padAngle(1 / innerRadius);
    },
    arc: function () {
      d3.arc().innerRadius(innerRadius).outerRadius(outerRadius);
    },

    ticks: function (group) {
      const startAngle = group.startAngle;
      const endAngle = group.endAngle;
      const value = group.value;
      const k = (endAngle - startAngle) / value; // (arc degrees)/group value = tick position/angle?
      return d3.range(0, value, tickStep).map((value) => {
        return { value, angle: value * k + startAngle };
      });
    },

    createLinksGroup: function (svg) {
      const linksGroup = svg.append("g").attr("fill-opacity", 0.8);

      return linksGroup;
    },
    updateLinksGroup: function (linksGroup, generatedData) {
      const links = linksGroup
        .selectAll("path")
        .data(generatedData)
        .join("path")
        .style("mix-blend-mode", "multiply")
        .attr("fill", (d) => color(names[d.source.index]))
        .attr("d", ribbon)
        .append("title")
        .text(
          (d) =>
            `${d.source.value} ${names[d.source.index]} -> ${
              names[d.target.index]
            }\n${d.target.value} ${names[d.target.index]} -> ${
              names[d.source.index]
            }`
        );
    },

    createNodesGroup: function (svg) {
      const nodesGroup = svg
        .append("g")
        .attr("font-size", 16)
        .attr("font-family", "sans-serif");
      return nodesGroup;
    },

    updateNodesGroup: function (nodesGroup, generatedData) {
      nodesGroup.selectAll("g").data(generatedData.groups).join("g");

      nodesGroup
        .append("path")
        .attr("fill", (d) => color(names[d.index]))
        .attr("d", (d) => arc(d));

      nodesGroup.append("title").text((d) => d.value);
    },
  },

  mounted() {
    const chord = d3
      .chordDirected()
      .padAngle(10 / innerRadius)
      .sortSubgroups(d3.descending)
      .sortChords(d3.descending);

    const tickStep = d3.tickStep(0, d3.sum(matrix.flat()), 10);
    const svg = d3
      .select("#chord-container")
      .append("svg")
      .attr("viewBox", [-width / 2, -height / 2, width, height]);

    const chords = chord(matrix);
  },
};
</script>

<style scoped>
</style>