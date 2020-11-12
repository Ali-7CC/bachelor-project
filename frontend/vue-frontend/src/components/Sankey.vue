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
  data() {
    return {
      colorScale: {},
      svg: {},
      WIDTH: 900,
      HEIGHT: 400,
      alignment: "sankeyLeft",
      color: "input",
      isGrouped: "false",
      ungroupedData: {
        nodes: [
          {
            name: "Call Outbound_1",
          },
          {
            name: "Handle Email_2",
          },
          {
            name: "Handle Case_2",
          },
          {
            name: "Call Outbound_3",
          },
          {
            name: "Handle Case_4",
          },
          {
            name: "Call Outbound_5",
          },
          {
            name: "Handle Email_0",
          },
          {
            name: "Inbound Email_1",
          },
          {
            name: "Handle Email_1",
          },
          {
            name: "Inbound Call_0",
          },
          {
            name: "Handle Case_1",
          },
          {
            name: "Inbound Call_1",
          },
          {
            name: "Call Outbound_2",
          },
          {
            name: "Inbound Call_2",
          },
          {
            name: "Inbound Call_3",
          },
          {
            name: "Inbound Email_0",
          },
          {
            name: "Email Outbound_1",
          },
          {
            name: "Handle Case_3",
          },
          {
            name: "Handle Case_0",
          },
          {
            name: "Email Outbound_2",
          },
          {
            name: "Inbound Call_4",
          },
          {
            name: "Call Outbound_4",
          },
        ],
        links: [
          {
            source: "Call Outbound_1",
            value: 1,
            target: "Handle Email_2",
          },
          {
            source: "Handle Case_2",
            value: 1,
            target: "Call Outbound_3",
          },
          {
            source: "Handle Case_4",
            value: 1,
            target: "Call Outbound_5",
          },
          {
            source: "Inbound Email_1",
            value: 1,
            target: "Handle Case_2",
          },
          {
            source: "Handle Email_1",
            value: 1,
            target: "Handle Case_2",
          },
          {
            source: "Inbound Call_0",
            value: 5,
            target: "Handle Case_1",
          },
          {
            source: "Inbound Call_1",
            value: 1,
            target: "Call Outbound_2",
          },
          {
            source: "Handle Email_0",
            value: 2,
            target: "Call Outbound_1",
          },
          {
            source: "Handle Email_0",
            value: 1,
            target: "Handle Email_1",
          },
          {
            source: "Inbound Call_0",
            value: 6,
            target: "Inbound Call_1",
          },
          {
            source: "Inbound Call_2",
            value: 1,
            target: "Inbound Call_3",
          },
          {
            source: "Inbound Email_0",
            value: 2,
            target: "Email Outbound_1",
          },
          {
            source: "Handle Case_2",
            value: 1,
            target: "Inbound Call_3",
          },
          {
            source: "Handle Case_2",
            value: 2,
            target: "Handle Case_3",
          },
          {
            source: "Handle Case_0",
            value: 1,
            target: "Handle Case_1",
          },
          {
            source: "Call Outbound_2",
            value: 1,
            target: "Call Outbound_3",
          },
          {
            source: "Email Outbound_1",
            value: 1,
            target: "Email Outbound_2",
          },
          {
            source: "Email Outbound_1",
            value: 1,
            target: "Handle Email_2",
          },
          {
            source: "Handle Case_1",
            value: 1,
            target: "Call Outbound_2",
          },
          {
            source: "Inbound Email_0",
            value: 1,
            target: "Handle Case_1",
          },
          {
            source: "Inbound Email_0",
            value: 1,
            target: "Call Outbound_1",
          },
          {
            source: "Inbound Call_1",
            value: 1,
            target: "Handle Case_2",
          },
          {
            source: "Inbound Call_0",
            value: 2,
            target: "Call Outbound_1",
          },
          {
            source: "Inbound Call_1",
            value: 1,
            target: "Inbound Call_2",
          },
          {
            source: "Handle Case_3",
            value: 1,
            target: "Inbound Call_4",
          },
          {
            source: "Handle Case_1",
            value: 2,
            target: "Inbound Call_2",
          },
          {
            source: "Inbound Call_3",
            value: 2,
            target: "Inbound Call_4",
          },
          {
            source: "Inbound Email_0",
            value: 1,
            target: "Inbound Email_1",
          },
          {
            source: "Handle Case_1",
            value: 2,
            target: "Handle Case_2",
          },
          {
            source: "Call Outbound_3",
            value: 1,
            target: "Call Outbound_4",
          },
          {
            source: "Call Outbound_3",
            value: 1,
            target: "Handle Case_4",
          },
          {
            source: "Handle Case_1",
            value: 1,
            target: "Email Outbound_2",
          },
        ],
      },
      groupedData: {
        nodes: [
          {
            name: "Call Outbound_2_(Case32)",
          },
          {
            name: "Call Outbound_3_(Case32)",
          },
          {
            name: "Handle Email_1_(Case43)",
          },
          {
            name: "Handle Case_2_(Case43)",
          },
          {
            name: "Call Outbound_3_(Case43)",
          },
          {
            name: "Handle Case_4_(Case43)",
          },
          {
            name: "Email Outbound_1_(G1_Inbound EmailEmail Outbound)",
          },
          {
            name: "Handle Email_2_(Case51)",
          },
          {
            name: "Inbound Call_2_(G2_Inbound CallHandle CaseInbound Call)",
          },
          {
            name: "Inbound Call_3_(Case38)",
          },
          {
            name: "Handle Case_0_(Case20)",
          },
          {
            name: "Handle Case_1_(Case20)",
          },
          {
            name: "Handle Email_0_(G0_Handle Email)",
          },
          {
            name: "End",
          },
          {
            name: "Handle Case_1_(G1_Inbound CallHandle Case)",
          },
          {
            name: "Call Outbound_2_(Case1)",
          },
          {
            name: "Inbound Call_0_(G0_Inbound Call)",
          },
          {
            name: "Inbound Email_0_(G0_Inbound Email)",
          },
          {
            name: "Handle Case_1_(Case54)",
          },
          {
            name: "Handle Case_3_(Case17)",
          },
          {
            name: "Inbound Call_4_(Case17)",
          },
          {
            name: "Call Outbound_1_(Case9)",
          },
          {
            name: "Handle Email_2_(Case9)",
          },
          {
            name: "Email Outbound_2_(Case54)",
          },
          {
            name: "Inbound Email_1_(Case19)",
          },
          {
            name: "Inbound Call_1_(G1_Inbound CallInbound Call)",
          },
          {
            name: "Handle Case_2_(Case47)",
          },
          {
            name: "Inbound Call_3_(Case47)",
          },
          {
            name: "Handle Case_2_(Case17)",
          },
          {
            name: "Call Outbound_1_(G1_Inbound CallCall Outbound)",
          },
          {
            name: "Inbound Call_4_(Case47)",
          },
          {
            name: "Handle Case_2_(Case19)",
          },
          {
            name: "Inbound Call_4_(Case38)",
          },
          {
            name: "Email Outbound_2_(Case40)",
          },
          {
            name: "Call Outbound_5_(Case43)",
          },
          {
            name: "Handle Case_3_(Case19)",
          },
          {
            name: "Call Outbound_1_(G1_Handle EmailCall Outbound)",
          },
          {
            name: "Call Outbound_4_(Case32)",
          },
          {
            name: "Inbound Call_2_(Case6)",
          },
          {
            name: "Handle Case_2_(Case20)",
          },
        ],
        links: [
          {
            source: "Call Outbound_2_(Case32)",
            value: 1,
            target: "Call Outbound_3_(Case32)",
          },
          {
            source: "Handle Email_1_(Case43)",
            value: 1,
            target: "Handle Case_2_(Case43)",
          },
          {
            source: "Call Outbound_3_(Case43)",
            value: 1,
            target: "Handle Case_4_(Case43)",
          },
          {
            source: "Email Outbound_1_(G1_Inbound EmailEmail Outbound)",
            value: 1,
            target: "Handle Email_2_(Case51)",
          },
          {
            source: "Inbound Call_2_(G2_Inbound CallHandle CaseInbound Call)",
            value: 1,
            target: "Inbound Call_3_(Case38)",
          },
          {
            source: "Handle Case_0_(Case20)",
            value: 1,
            target: "Handle Case_1_(Case20)",
          },
          {
            source: "Handle Email_0_(G0_Handle Email)",
            value: 1,
            target: "End",
          },
          {
            source: "Handle Case_1_(G1_Inbound CallHandle Case)",
            value: 1,
            target: "Call Outbound_2_(Case1)",
          },
          {
            source: "Inbound Call_0_(G0_Inbound Call)",
            value: 31,
            target: "End",
          },
          {
            source: "Handle Case_2_(Case43)",
            value: 1,
            target: "Call Outbound_3_(Case43)",
          },
          {
            source: "Inbound Email_0_(G0_Inbound Email)",
            value: 1,
            target: "Handle Case_1_(Case54)",
          },
          {
            source: "Handle Case_3_(Case17)",
            value: 1,
            target: "Inbound Call_4_(Case17)",
          },
          {
            source: "Call Outbound_1_(Case9)",
            value: 1,
            target: "Handle Email_2_(Case9)",
          },
          {
            source: "Handle Case_1_(Case54)",
            value: 1,
            target: "Email Outbound_2_(Case54)",
          },
          {
            source: "Inbound Email_0_(G0_Inbound Email)",
            value: 1,
            target: "Inbound Email_1_(Case19)",
          },
          {
            source: "Inbound Call_1_(G1_Inbound CallInbound Call)",
            value: 1,
            target: "Call Outbound_2_(Case32)",
          },
          {
            source: "Handle Case_2_(Case47)",
            value: 1,
            target: "Inbound Call_3_(Case47)",
          },
          {
            source: "Handle Case_1_(G1_Inbound CallHandle Case)",
            value: 1,
            target: "Handle Case_2_(Case17)",
          },
          {
            source: "Inbound Call_0_(G0_Inbound Call)",
            value: 2,
            target: "Call Outbound_1_(G1_Inbound CallCall Outbound)",
          },
          {
            source: "Inbound Call_3_(Case47)",
            value: 1,
            target: "Inbound Call_4_(Case47)",
          },
          {
            source: "Inbound Email_1_(Case19)",
            value: 1,
            target: "Handle Case_2_(Case19)",
          },
          {
            source: "Inbound Call_3_(Case38)",
            value: 1,
            target: "Inbound Call_4_(Case38)",
          },
          {
            source: "Email Outbound_1_(G1_Inbound EmailEmail Outbound)",
            value: 1,
            target: "Email Outbound_2_(Case40)",
          },
          {
            source: "Handle Case_4_(Case43)",
            value: 1,
            target: "Call Outbound_5_(Case43)",
          },
          {
            source: "Inbound Call_0_(G0_Inbound Call)",
            value: 6,
            target: "Inbound Call_1_(G1_Inbound CallInbound Call)",
          },
          {
            source: "Inbound Email_0_(G0_Inbound Email)",
            value: 2,
            target: "Email Outbound_1_(G1_Inbound EmailEmail Outbound)",
          },
          {
            source: "Handle Case_2_(Case19)",
            value: 1,
            target: "Handle Case_3_(Case19)",
          },
          {
            source: "Handle Case_2_(Case17)",
            value: 1,
            target: "Handle Case_3_(Case17)",
          },
          {
            source: "Handle Email_0_(G0_Handle Email)",
            value: 1,
            target: "Handle Email_1_(Case43)",
          },
          {
            source: "Handle Email_0_(G0_Handle Email)",
            value: 2,
            target: "Call Outbound_1_(G1_Handle EmailCall Outbound)",
          },
          {
            source: "Inbound Email_0_(G0_Inbound Email)",
            value: 1,
            target: "Call Outbound_1_(Case9)",
          },
          {
            source: "Call Outbound_3_(Case32)",
            value: 1,
            target: "Call Outbound_4_(Case32)",
          },
          {
            source: "Inbound Call_1_(G1_Inbound CallInbound Call)",
            value: 1,
            target: "Inbound Call_2_(Case6)",
          },
          {
            source: "Inbound Call_0_(G0_Inbound Call)",
            value: 5,
            target: "Handle Case_1_(G1_Inbound CallHandle Case)",
          },
          {
            source: "Handle Case_1_(G1_Inbound CallHandle Case)",
            value: 2,
            target: "Inbound Call_2_(G2_Inbound CallHandle CaseInbound Call)",
          },
          {
            source: "Handle Case_1_(Case20)",
            value: 1,
            target: "Handle Case_2_(Case20)",
          },
          {
            source: "Inbound Call_1_(G1_Inbound CallInbound Call)",
            value: 1,
            target: "Handle Case_2_(Case47)",
          },
        ],
      },
    };
  },

  methods: {
    /**
     * Creates the three SVG group elements that make up the diagram
     */
    createGroups: function (generatedData, svg) {
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
    // Generating the data for the elements
    this.generatedData =
      this.isGrouped === "true"
        ? this.sankeyGenerator(this.groupedData)
        : this.sankeyGenerator(this.ungroupedData);
    // Updating the group
    this.createGroups(this.generatedData, this.svg);
    this.updateGroups(
      this.nodeGroup,
      this.namesGroup,
      this.linksGroup,
      this.generatedData
    );
  },

  updated() {
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
