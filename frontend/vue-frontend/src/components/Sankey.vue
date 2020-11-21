<template>
  <div>
    <h1>Sankey Diagram</h1>
    <div id="view-options">
      <select v-model="alignment" id="alignment">
        <option value="sankeyLeft">Left aligned</option>
        <option value="sankeyRight">Right aligned</option>
        <option value="sankeyCenter">Centered</option>
        <option value="sankeyJustify">Justified</option>
      </select>

      <select @change="onColorChange" id="color">
        <option value="input">Color by input</option>
        <option value="output">Color by output</option>
      </select>

      <select v-model="isGrouped" id="is-grouped">
        <option value="false">No groups</option>
        <option value="true">Group traces</option>
      </select>
    </div>
    <div id="sankey-container"></div>
  </div>
</template>

<script>
import * as d3 from "d3";
import * as d3Sankey from "d3-sankey";

export default {
  props: ["sankeyData"],
  data() {
    return {
      WIDTH: 900,
      HEIGHT: 400,
      alignment: "sankeyLeft",
      isGrouped: "false",
      // Raw data from parent component
      groupedData: { nodes: [], links: [] },
      ungroupedData: { nodes: [], links: [] },
    };
  },

  computed: {
    /**
     * Adds the svg element to the DOM
     */
    svg: function () {
      return d3
        .select("#sankey-container")
        .append("svg")
        .attr("viewBox", [0, 0, this.WIDTH, this.HEIGHT]);
    },

    /**
     * Defines the color scale with the range schemeCategory10
     */
    colorScale: function () {
      return d3.scaleOrdinal(d3.schemeCategory10);
    },

    /**
     * Defines the sankey generator with configuration based on the component's data
     */
    sankeyGenerator: function () {
      return (
        d3Sankey
          .sankey()
          .nodeId((d) => d.name)
          .nodeWidth(15)
          .nodePadding(5)
          .nodeAlign(d3Sankey[this.alignment])
          // Defines the layout. [left, top], [right, bottom]
          .extent([
            [0, 0],
            [this.WIDTH, this.HEIGHT],
          ])
      );
    },

    /**
     * Computes the data used to draw the Sankey diagram using the Sankey generator and the raw data.
     */
    generatedData: function () {
      const data =
        this.isGrouped === "true"
          ? this.sankeyGenerator(this.groupedData)
          : this.sankeyGenerator(this.ungroupedData);
      return data;
    },

    /**
     * SVG group element that holds the nodes.
     * A node consists of a rect element that contains a title element
     */
    nodesGroup: function () {
      return this.svg
        .append("g")
        .attr("stroke", "#000")
        .attr("stroke-width", 1);
    },

    /**
     * SVG group element that holds node labels of the Sankey diagram.
     *  A label is represented by an SVG text element.
     */
    namesGroup: function () {
      return this.svg
        .append("g")
        .attr("font-family", "sans-serif")
        .attr("font-size", 8)
        .attr("font-weight", "bold");
    },

    /**
     * SVG group that holds the links of the Sankey diagram.
     * A link consists of a group that contains a path and a title element
     */
    linksGroup: function () {
      return this.svg
        .append("g")
        .attr("fill", "none")
        .attr("stroke-opacity", 0.5);
    },
  },

  watch: {
    /**
     * Watching changes to the data passed from the parent component to rerender the diagram
     */
    sankeyData: function () {
      this.groupedData = Object.assign({}, this.groupedData, {
        nodes: this.sankeyData.groupedModel.nodes,
        links: this.sankeyData.groupedModel.links,
      });
      this.ungroupedData = Object.assign({}, this.ungroupedData, {
        nodes: this.sankeyData.ungroupedModel.nodes,
        links: this.sankeyData.ungroupedModel.links,
      });
      this.$forceUpdate();
    },
  },

  methods: {
    /**
     * Updates nodesGroup when data changes
     */
    updateNodesGroup: function () {
      // Update the rect element (add new, remove old) for the new data set
      // For each new rect, add am empty title element to it
      this.nodesGroup
        .selectAll("rect")
        .data(this.generatedData.nodes)
        .join((enter) => enter.append("rect").append("title"));

      // For all rect elements, update their attributes
      this.nodesGroup
        .selectAll("rect")
        .attr("x", (d) => d.x0)
        .attr("y", (d) => d.y0)
        .attr("height", (d) => d.y1 - d.y0)
        .attr("width", (d) => d.x1 - d.x0)
        .attr("fill", (d) => this.colorScale(d.name.split("_")[0]));

      // For all title elements inside rect elements, update their text
      this.nodesGroup
        .selectAll("rect")
        .select("title")
        .text((d) => `${d.name.split("_")[0]}\n${d.value}`);
    },

    /**
     * Updates namesGroup when data changes
     */
    updateNamesGroup: function () {
      this.namesGroup
        .selectAll("text")
        .data(this.generatedData.nodes)
        .join("text")
        .attr("x", (d) => (d.x0 < this.WIDTH / 2 ? d.x1 + 6 : d.x0 - 6))
        .attr("y", (d) => (d.y1 + d.y0) / 2)
        .attr("dy", "0.35em")
        // Tags the labels with eitehr "start" or "end" based on their position
        .attr("text-anchor", (d) => (d.x0 < this.WIDTH / 2 ? "start" : "end"))
        .text((d) => d.name.split("_")[0]);
    },

    /**
     * Updates linksGroup when data changes
     */
    updateLinksGroup: function () {
      // Update the groups (add new, remove old) for the new data
      // For each new group, add an empty path element to it
      this.linksGroup
        .selectAll("g")
        .data(this.generatedData.links)
        .join((enter) => {
          const newLinkGroup = enter.append("g");
          newLinkGroup.append("path");
          newLinkGroup.append("title");
        });

      // Selecting the links groups
      const linksGroups = this.linksGroup.selectAll("g");

      // For each group, update its path attribute
      linksGroups
        .select("path")
        .attr("d", d3Sankey.sankeyLinkHorizontal())
        .attr("stroke", (d) => this.colorScale(d.source.name.split("_")[0]))
        .attr("stroke-width", (d) => Math.max(1, d.width));

      // For each group, update its title element
      linksGroups
        .select("title")
        .text(
          (d) =>
            `${d.source.name.split("_")[0]} → ${
              d.target.name.split("_")[0].split("_")[0]
            }\n${d.value}`
        );
    },

    /**
     * Changes the color attribute for the links path elements.
     * Using an event listener to change one attribute instead of redrawing the entire diagram
     * (compared to alignment or groupping sliders)
     */
    onColorChange: function (e) {
      const option = e.target.value;
      if (option === "input") {
        this.linksGroup
          .selectAll("g")
          .select("path")
          .attr("stroke", (d) => this.colorScale(d.source.name.split("_")[0]));
      } else {
        this.linksGroup
          .selectAll("g")
          .select("path")
          .attr("stroke", (d) => this.colorScale(d.target.name.split("_")[0]));
      }
    },
  },

  beforeUpdate() {
    this.updateNodesGroup();
    this.updateNamesGroup();
    this.updateLinksGroup();
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
  font-size: 30px;
}

#view-options select {
  margin-left: 20px;
}

#sankey-container {
  margin: 5px 5px 0 20px;
}
</style>
