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
  props: ["rawData"],
  data() {
    return {
      WIDTH: 1200,
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
      return d3.scaleOrdinal(d3.schemeTableau10);
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

    noData: function () {
      return (
        this.groupedData.nodes.length === 0 ||
        this.ungroupedData.nodes.length === 0
      );
    },
  },

  watch: {
    /**
     * Watching changes to the data passed from the parent component to rerender the diagram
     */
    rawData: function () {
      this.groupedData = Object.assign({}, this.groupedData, {
        nodes: this.rawData.groupedModel.nodes,
        links: this.rawData.groupedModel.links,
      });
      this.ungroupedData = Object.assign({}, this.ungroupedData, {
        nodes: this.rawData.ungroupedModel.nodes,
        links: this.rawData.ungroupedModel.links,
      });
      this.$forceUpdate();
    },
  },

  methods: {
    /**
     * Updates nodesGroup when data changes
     */
    updateNodesGroup: function () {
      // The update selection
      const nodes = this.nodesGroup
        .selectAll("rect")
        .data(this.generatedData.nodes);
      // .join((enter) => enter.append("rect").append("title"));

      // Removing the exit selection
      nodes.exit().remove();

      // The enter selection
      const newNodes = nodes.enter().append("rect");
      newNodes.append("title");

      // The merged selection
      const allNodes = newNodes.merge(nodes);

      // For all rect elements, update their attributes
      allNodes
        .attr("x", (d) => d.x0)
        .attr("y", (d) => d.y0)
        .attr("height", (d) => d.y1 - d.y0)
        .attr("width", (d) => d.x1 - d.x0)
        .attr("fill", (d) => this.colorScale(d.name.split("_")[0]));

      // For all title elements inside rect elements, update their text
      allNodes
        .select("title")
        .text((d) => `${d.name.split("_")[0]}\n${d.value}`);
    },

    /**
     * Updates namesGroup when data changes
     */
    updateNamesGroup: function () {
      // The update selection
      const names = this.namesGroup
        .selectAll("text")
        .data(this.generatedData.nodes);

      // Removing the exit selection
      names.exit().remove();

      // The enter selection
      const newNames = names.enter().append("text");

      // Merged selection
      const allNames = newNames.merge(names);

      allNames
        .attr("x", (d) => (d.x0 < this.WIDTH / 2 ? d.x1 + 6 : d.x0 - 6))
        .attr("y", (d) => (d.y1 + d.y0) / 2)
        .attr("dy", "0.35em")
        // Tags the labels with eitehr "start" or "end" based on their position
        .attr("text-anchor", (d) => (d.x0 < this.WIDTH / 2 ? "start" : "end"))
        .text((d) => d.name.split(/_[0-9]/)[0]);
    },

    /**
     * Updates linksGroup when data changes
     */
    updateLinksGroup: function () {
      // Update selection
      const links = this.linksGroup
        .selectAll("g")
        .data(this.generatedData.links);

      // Removing exit selection
      links.exit().remove();

      // Enter selection
      const newLinks = links.enter().append("g");
      newLinks.append("path");
      newLinks.append("title");

      // Merged selection
      const allLinks = newLinks.merge(links);

      // For each group, update its path attribute
      allLinks
        .select("path")
        .attr("d", d3Sankey.sankeyLinkHorizontal())
        .attr("stroke", (d) => this.colorScale(d.source.name.split("_")[0]))
        .attr("stroke-width", (d) => Math.max(1, d.width));

      // For each group, update its title element
      allLinks
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
    if (this.noData) {
      this.nodesGroup.selectAll("*").remove();
      this.namesGroup.selectAll("*").remove();
      this.linksGroup.selectAll("*").remove();
    } else {
      this.updateNodesGroup();
      this.updateNamesGroup();
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

#sankey-container {
  margin: 5px 5px 0 20px;
}
</style>
