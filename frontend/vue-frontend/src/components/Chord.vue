<template>
  <div>
    <h1>Chord Diagram</h1>
    <div id="chord-container"></div>
  </div>
</template>

<script>
import * as d3 from "d3";
export default {
  props: ["rawData"],
  data() {
    return {
      width: 1400,
      height: 1000,
      // Raw data from parent component
      matrix: [],
      nodes: [],
    };
  },

  /**
   * Fires when new data is loaded from parent component
   */
  watch: {
    rawData: function () {
      this.matrix = this.rawData.matrix;
      this.nodes = this.rawData.nodes;
      this.$forceUpdate();
    },
  },

  computed: {
    outerRadius: function () {
      return Math.min(this.width, this.height) * 0.5 - 150;
    },
    innerRadius: function () {
      return this.outerRadius - 10;
    },
    /**
     * Adds the svg element to the DOM
     */
    svg: function () {
      return d3
        .select("#chord-container")
        .append("svg")
        .attr("viewBox", [
          -this.width / 2,
          -this.height / 2,
          this.width,
          this.height,
        ]);
    },
    /**
     * Defines the color scale with the range schemeCategory10
     */
    colorScale: function () {
      return d3.scaleOrdinal(d3.schemeCategory10);
    },
    /**
     * Draws the diagram's nodes
     */
    arc: function () {
      return d3
        .arc()
        .innerRadius(this.innerRadius)
        .outerRadius(this.outerRadius);
    },

    /**
     * Draws the diagram's links
     */
    ribbon: function () {
      return d3
        .ribbonArrow()
        .radius(this.innerRadius - 1)
        .padAngle(1 / this.innerRadius);
    },

    /**
     * Defines the Chord generator with configuration based on the component's data
     */
    chordGenerator: function () {
      return d3
        .chordDirected()
        .padAngle(10 / this.innerRadius)
        .sortSubgroups(d3.descending)
        .sortChords(d3.descending);
    },
    /**
     * Computes the data used to draw the Chord diagram using the Chord generator and the raw data.
     */
    generatedChords: function () {
      return this.chordGenerator(this.matrix);
    },

    /**
     * d3.tickStep(start, stop, count) -> tickstep:float
     * returns a float that represents the step amount
     */
    tickStep: function () {
      return d3.tickStep(0, d3.sum(this.matrix.flat()), 10);
    },

    /**
     * SVG group element that holds the nodes.
     * A node consists of a path element, a title element, and a group
     * of ticksGroup, which holds a line element (for the tick) and a text element for the name
     */
    nodesGroup: function () {
      console.log("nodes group computed");
      return this.svg
        .append("g")
        .attr("font-size", 16)
        .attr("font-family", "sans-serif");
    },

    /**
     * SVG group that holds the links of the Sankey diagram.
     * A link consists of a path element which contains a title element
     */
    linksGroup: function () {
      return this.svg.append("g").attr("fill-opacity", 0.8);
    },
  },

  methods: {
    /**
     * Updates nodesGroup
     */
    updateNodesGroup: function () {
      // Update the groups (add new, remove old) for each datum
      // For each new group, add to it a path, a title, and a group element (for ticks)
      this.nodesGroup
        .selectChildren("g")
        .data(this.generatedChords.groups)
        .join((enter) => {
          const newGroup = enter.append("g");
          newGroup.append("path");
          newGroup.append("title");
          newGroup.append("g");
        });

      // Selects the nodes
      const updatedNodes = this.nodesGroup.selectChildren("g");

      // Update the paths
      updatedNodes
        .select("path")
        .attr("fill", (d) => this.colorScale(this.nodes[d.index]))
        .attr("d", (d) => this.arc(d));

      // Update the title
      updatedNodes.select("title").text((d) => d.value);

      // Update the ticks group
      // a tick group is a group element that contains ticks
      // Each tick is a group element that has a line and a text
      updatedNodes
        .select("g")
        .selectAll("g")
        .data((d) => this.ticks(d)) // {value, angle:<angle>}
        .join((enter) => {
          const newTickGroup = enter.append("g");
          newTickGroup.append("line");
          newTickGroup.append("text");
        });

      // Selecting the ticksGroups
      const ticksGroups = updatedNodes.select("g").selectChildren("g");

      // Updating the ticks' postion, line and text
      ticksGroups.attr(
        "transform",
        (d) =>
          `rotate(${(d.angle * 180) / Math.PI - 90}) translate(${
            this.outerRadius
          },0)`
      );

      ticksGroups
        .selectAll("line")
        .attr("stroke", "currentColor")
        .attr("x2", 6);
      ticksGroups
        .selectAll("text")
        .attr("x", 8)
        .attr("dy", "0.35em")
        .attr("transform", (d) =>
          d.angle > Math.PI ? "rotate(180) translate(-16)" : null
        )
        .attr("text-anchor", (d) => (d.angle > Math.PI ? "end" : null))
        .text((d) => d.value);

      // Updates the text of the first tick
      updatedNodes.select("text").attr("font-weight", "bold").text((d, i, n) => {
        return n[i].getAttribute("text-anchor") === "end"
          ? `↑ ${this.nodes[d.index]}`
          : `${this.nodes[d.index]} ↓`;
      });
    },
    /**
     * Updates linksGroup
     */
    updateLinksGroup: function () {
      // Updates the links (add new, remove old) for the new data
      // For each new link, add an empty path element to it
      this.linksGroup
        .selectAll("path")
        .data(this.generatedChords)
        .join((enter) => enter.append("path").append("title"));

      const links = this.linksGroup.selectAll("path");

      // Updating the link's paths
      links
        .style("mix-blend-mode", "multiply")
        .attr("fill", (d) => this.colorScale(this.nodes[d.source.index]))
        .attr("d", this.ribbon);

      // Updating the link's title
      links
        .selectAll("title")
        .text(
          (d) =>
            `${d.source.value} ${this.nodes[d.source.index]} -> ${
              this.nodes[d.target.index]
            }\n${d.target.value} ${this.nodes[d.target.index]} -> ${
              this.nodes[d.source.index]
            }`
        );
    },

    /** 
    Returns {value, angle:<angle>}
    value ranges from 0 to the max value of the group
   angle is the position of the tick
    */
    ticks: function (group) {
      const startAngle = group.startAngle;
      const endAngle = group.endAngle;
      const value = group.value;
      const k = (endAngle - startAngle) / value; // (arc degrees)/group value = tick position/angle?
      return d3.range(0, value, this.tickStep).map((value) => {
        return { value, angle: value * k + startAngle };
      });
    },
  },

  beforeUpdate() {
    this.updateNodesGroup();
    this.updateLinksGroup();
  },
};
</script>

<style scoped></style>
