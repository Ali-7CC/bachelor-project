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

      <select v-model="color" id="color">
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
  props : ["sankeyData"],
  data() {
    return {
      colorScale: {},
      svg: {},
      WIDTH: 900,
      HEIGHT: 400,
      alignment: "sankeyLeft",
      color: "input",
      isGrouped: "false",
      groupedData : {},
      ungroupedData : {},
      // groups
      nodeGroup : null,
      namesGroup : null,
      linksGroup : null
      // Group elements

    };
  },

  watch : {
    sankeyData : function(){
      this.groupedData = this.sankeyData.groupedModel;
      this.ungroupedData = this.sankeyData.ungroupedModel;
      this.$forceUpdate();
    }

  },

  methods: {
    /**
     * Creates the three SVG group elements that make up the diagram
     */
    createGroups: function (svg) {
      this.nodeGroup = this.createNodeGroup(svg);
      this.namesGroup = this.createNamesGroup(svg);
      this.linksGroup = this.createLinksGroup(svg);
    },

    /**
     * Updates the three SVG group elements when any of the diagrams data changes.
     */
    updateGroups: function (nodeGroup, namesGroup, linksGroup, generatedData) {
      this.updateRects(nodeGroup, generatedData);
      this.updateNames(namesGroup, generatedData);
      this.updateLinksGroup(linksGroup, generatedData);
    },

    createNodeGroup: function (svg) {
      const nodeGroup = svg
        .append("g")
        .attr("stroke", "#000")
        .attr("stroke-width", 1);
      return nodeGroup;
    },

    updateRects: function (nodeGroup, generatedData) {
      nodeGroup
        .selectAll("rect")
        .data(generatedData.nodes)
        .join("rect")
        .attr("x", (d) => d.x0)
        .attr("y", (d) => d.y0)
        .attr("height", (d) => d.y1 - d.y0)
        .attr("width", (d) => d.x1 - d.x0)
        .attr("fill", (d) => this.colorScale(d.name.split("_")[0]));

      // Removing old title elements before appending.
      nodeGroup.selectAll("rect").selectAll("title").remove();
      nodeGroup
        .selectAll("rect")
        .append("title")
        .text((d) => `${d.name}\n${d.value}`);

      // Returning the rects
      const updatedRects = nodeGroup.selectAll("rect");

      return updatedRects;
    },

    createNamesGroup: function (svg) {
      const namesGroup = svg
        .append("g")
        .attr("font-family", "sans-serif")
        .attr("font-size", 8)
        .attr("font-weight", "bold");

      return namesGroup;
    },
    updateNames: function (namesGroup, generatedData) {
      const updateNames = namesGroup
        .selectAll("text")
        .data(generatedData.nodes)
        .join("text")
        .attr("x", (d) => (d.x0 < this.WIDTH / 2 ? d.x1 + 6 : d.x0 - 6))
        .attr("y", (d) => (d.y1 + d.y0) / 2)
        .attr("dy", "0.35em")
        .attr("text-anchor", (d) => (d.x0 < this.WIDTH / 2 ? "start" : "end"))
        .text((d) => d.name.split("_")[0]);

      return updateNames;
    },

    createLinksGroup: function (svg) {
      const linksGroup = svg
        .append("g")
        .attr("fill", "none")
        .attr("stroke-opacity", 0.5);

      return linksGroup;
    },

    updateLinksGroup: function (linksGroup, generatedData) {
      // Joining new groups
      const links = linksGroup
        .selectAll("g")
        .data(generatedData.links)
        .join("g");

      // Removing old paths and adding new ones
      links.selectAll("path").remove();
      links
        .append("path")
        .attr("d", d3Sankey.sankeyLinkHorizontal())
        .attr("stroke", (d) => {
          const option = document.querySelector("#color").value;
          console.log(option);
          if (this.color === "input") {
            return this.colorScale(d.source.name.split("_")[0]);
          } else {
            return this.colorScale(d.target.name.split("_")[0]);
          }
        })

        .attr("stroke-width", (d) => Math.max(1, d.width));

      // Removing old titles and adding new ones
      links.selectAll("title").remove();
      links
        .append("title")
        .text((d) => `${d.source.name} → ${d.target.name}\n${d.value}`);

      return linksGroup.selectAll("g");
    },
  },

  mounted() {
    // Creating an SVG element in the HTML
    this.svg = d3
      .select("#sankey-container")
      .append("svg")
      .attr("viewBox", [0, 0, this.WIDTH, this.HEIGHT]);

    // Creating the Sankey generator
    this.sankeyGenerator = d3Sankey
      .sankey()
      .nodeId((d) => d.name)
      .nodeWidth(15)
      .nodePadding(5)
      .nodeAlign(d3Sankey[this.alignment])
      // Defines the layout. [left, top], [right, bottom]
      .extent([
        [0, 0],
        [this.WIDTH, this.HEIGHT],
      ]);

    // Creating a color scale with the range shcemeCategory10
    this.colorScale = d3.scaleOrdinal(d3.schemeCategory10);

    // Creating the groups
        this.createGroups(this.svg);

//     // Generating the data for the elements
//     this.generatedData =
//       this.isGrouped === "true"
//         ? this.sankeyGenerator(this.groupedModel)
//         : this.sankeyGenerator(this.ungroupedModel
// );
//     // Updating the group
//     this.updateGroups(
//       this.nodeGroup,
//       this.namesGroup,
//       this.linksGroup,
//       this.generatedData
//     );
  },

  updated() {
    console.log(this.test);
    this.sankeyGenerator = d3Sankey
      .sankey()
      .nodeId((d) => d.name)
      .nodeWidth(15)
      .nodePadding(5)
      .nodeAlign(d3Sankey[this.alignment])
      .extent([
        [0, 0],
        [this.WIDTH, this.HEIGHT],
      ]);
    this.generatedData =
      this.isGrouped === "true"
        ? this.sankeyGenerator(this.groupedData)
        : this.sankeyGenerator(this.ungroupedData);

    this.updateGroups(
      this.nodeGroup,
      this.namesGroup,
      this.linksGroup,
      this.generatedData
    );
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
