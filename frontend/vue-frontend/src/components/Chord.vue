<template>
  <div>
    <h1>Chord Diagram</h1>
    <div id="view-options">
      <select v-model="diagramType">
        <option value="directed">Directed Diagarm</option>
        <option value="undirected">Undirected Diagram</option>
      </select>
    </div>
    <div id="chord-container"></div>
  </div>
</template>

<script>
import * as d3 from "d3";
export default {
  props: ["rawData"],
  data() {
    return {
      width: 1600,
      height: 900,
      diagramType: "directed",
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
      return d3.scaleOrdinal(d3.schemeTableau10);
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
      if (this.diagramType === "directed") {
        return d3
          .chordDirected()
          .padAngle(10 / this.innerRadius)
          .sortSubgroups(d3.descending)
          .sortChords(d3.descending);
      } else {
        return d3
          .chord()
          .padAngle(10 / this.innerRadius)
          .sortSubgroups(d3.descending)
          .sortChords(d3.descending);
      }
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

    noData: function () {
      return this.nodes.length === 0 || this.matrix.length === 0;
    },
  },

  methods: {
    /**
     * Updates nodesGroup
     */
    updateNodesGroup: function () {
      // The update selection
      const nodes = this.nodesGroup
        .selectChildren("g")
        .data(this.generatedChords.groups);

      // Removing the exit selection
      nodes.exit().remove();

      // Appending to the enter selection
      const newNodes = nodes.enter().append("g");
      newNodes.append("path");
      newNodes.append("title");
      newNodes.append("g");

      // Merging new and old nodes
      const allNodes = newNodes.merge(nodes);

      // Update the paths
      allNodes
        .select("path")
        .attr("fill", (d) => this.colorScale(this.nodes[d.index]))
        .attr("d", (d) => this.arc(d));

      // Update the title
      allNodes.select("title").text((d) => d.value);

      // Update the ticks group
      // a tick group is a group element that contains ticks
      // Each tick is a group element that has a line and a text
      const ticks = allNodes
        .select("g") // tick groups
        .selectAll("g") // ticks
        .data((d) => {
          return this.ticks(d); // {value, angle:<angle>}
        });

      ticks.exit().remove();

      const newTicks = ticks.enter().append("g");
      newTicks.append("line");
      newTicks.append("text");

      const allTicks = newTicks.merge(ticks);

      // Updating the ticks' postion, line and text
      allTicks.attr("transform", (d) => {
        return `rotate(${(d.angle * 180) / Math.PI - 90}) translate(${
          this.outerRadius
        },0)`;
      });
      // Updating the lines
      allTicks.selectAll("line").attr("stroke", "currentColor").attr("x2", 6);
      // Updating the text
      allTicks
        .selectAll("text")
        .attr("x", 8)
        .attr("dy", "0.35em")
        .attr("transform", (d) => {
          if (d.angle > Math.PI) {
            return "rotate(180) translate(-16)";
          } else {
            return null;
          }
        })
        .attr("text-anchor", (d) => (d.angle > Math.PI ? "end" : null))
        .text((d) => d.value);

      // Hack to not change the data bound to the text element when using the select
      const firstTicks = allNodes
        .selectAll("text")
        .filter((d) => d.value === 0);

      firstTicks.attr("font-weight", "bold").text((d, i, n) => {
        if (n[i].getAttribute("text-anchor") === "end") {
          return `↑ ${this.nodes[d.index]}`;
        } else {
          return `${this.nodes[d.index]} ↓`;
        }
      });
    },

    /**
     * Updates linksGroup based on the new data
     */
    updateLinksGroup: function () {
      // Joining data to get the update selection
      const links = this.linksGroup
        .selectAll("path")
        .data(this.generatedChords);

      // Removing the exit selection
      links.exit().remove();

      // Appending new paths to the enter selection
      const newLinks = links.enter().append("path");

      // Mergeing the old and new
      const allLinks = newLinks.merge(links);

      // Updating the link's path attributes
      allLinks
        .style("mix-blend-mode", "multiply")
        .attr("fill", (d) => this.colorScale(this.nodes[d.source.index]))
        .attr("d", this.ribbon);

      // Appending titles to the new links
      const newTitles = newLinks.append("title");

      // Mereging the old and new titles
      const allTitles = newTitles.merge(links.select("title"));

      // Updating the text of the titles
      if (this.diagramType === "directed") {
        allTitles.text(
          (d) =>
            `${this.nodes[d.source.index]} → ${this.nodes[d.target.index]} ${
              d.source.value
            }`
        );
      } else {
        allTitles.text(
          (d) =>
            `${d.source.value} ${this.nodes[d.source.index]} → ${
              this.nodes[d.target.index]
            }${
              d.source.index === d.target.index
                ? ""
                : `\n${d.target.value} ${this.nodes[d.target.index]} → ${
                    this.nodes[d.source.index]
                  }`
            }`
        );
      }

      links.exit().remove();
    },

    /** 
    Returns {index, value, angle:<angle>}
    value ranges from 0 to the max value of the group
   angle is the position of the tick
    */
    ticks: function (group) {
      const startAngle = group.startAngle;
      const endAngle = group.endAngle;
      const value = group.value;
      let k = null;
      if (value === 0) {
        k = 0;
      } else {
        k = (endAngle - startAngle) / value; // (arc degrees)/group value = tick position/angle?}
      }
      return d3.range(0, value, this.tickStep).map((value) => {
        value = Number(value.toFixed(2));
        return { index: group.index, value, angle: value * k + startAngle };
      });
    },
  },

  beforeUpdate() {
    if (this.noData) {
      this.nodesGroup.selectAll("*").remove();
      this.linksGroup.selectAll("*").remove();
    } else {
      // Temp hack until the ticks bug is fixed
      this.nodesGroup.selectAll("*").remove();
      this.linksGroup.selectAll("*").remove();
      this.updateNodesGroup();
      this.updateLinksGroup();
    }
  },
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

h1 {
  margin: 5px 20px;
  font-size: 25px;
}

#view-options select {
  margin-left: 20px;
}

#chord-container {
  margin: 5px 5px 0 20px;
}
</style>
