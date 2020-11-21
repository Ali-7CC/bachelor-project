<template>
  <div>
    <h1>Process Data Visualization</h1>
    <div id="upload-container">
      <input type="file" accept=".xes" v-on:change="onFileSelected" />
      <button v-on:click="onUpload">Upload</button>
    </div>
    <div id="draw-container">
      <select v-model="selectedFileDraw">
        <option value="null">Select a file</option>
        <option v-for="file in this.files" v-bind:key="file.name">
          {{ file.name }}
        </option>
      </select>
      <select v-model="selectedAttr">
        <option value="">Select an attribute</option>
        <template v-if="selectedFileDraw != null">
          <option
            v-for="attr in files.find((f) => f.name === selectedFileDraw)
              .attributes"
            v-bind:key="attr"
          >
            {{ attr }}
          </option>
        </template>
      </select>
      <select v-model="selectedOp">
        <option value="">Select an operation</option>
        <option value="COUNT">Count</option>
        <option value="SUM">Sum</option>
        <option value="DIFF">Difference</option>
      </select>
      <select v-model="selectedAgg">
        <option value="">Select an aggregator</option>
        <option value="SUM">Sum</option>
        <option value="MAX">Max</option>
        <option value="MIN">Min</option>
        <option value="AVG">Average</option>
      </select>
      <button v-on:click="onDraw">Draw</button>
    </div>
    <div id="nav">
      <select v-model="selectedVis">
        <option value="sankey" selected>Sankey</option>
        <option value="chord">Chord</option>
      </select>
    </div>
    <Sankey :sankey-data="sankeyData"></Sankey>
    <Chord :chord-data="chordData"></Chord>
  </div>
</template>

<script>
import axios from "axios";
import Sankey from "./Sankey.vue";
import Chord from "./Chord.vue";

export default {
  components: {
    Sankey: Sankey,
    Chord: Chord,
  },
  data() {
    return {
      // File upload
      selectedFileUpload: "",
      files: [],
      // Drawing parameters
      selectedFileDraw: null,
      selectedAttr: "",
      selectedOp: "",
      selectedAgg: "",
      selectedVis: "sankey",
      // Sankey data
      sankeyData: "",
      // Chord Data
      chordData: "",
    };
  },

  methods: {
    onFileSelected: function (event) {
      this.selectedFileUpload = event.target.files[0];
    },

    onUpload: function () {
      const payload = new FormData();
      payload.append("file", this.selectedFileUpload);
      axios.post("http://localhost:8080/upload", payload).then((res) => {
        const newFile = {
          name: this.selectedFileUpload.name,
          attributes: res.data.validAttributes,
        };
        this.files.push(newFile);
        this.selectedFileUpload = null;
      });
    },

    onDraw: function () {
      const payload = new FormData();
      payload.append("fileName", this.selectedFileDraw);
      payload.append("attributeKey", this.selectedAttr);
      payload.append("operation", this.selectedOp);
      payload.append("aggregationFunc", this.selectedAgg);
      if (this.selectedVis === "sankey") {
        axios
          .post("http://localhost:8080/createSankey", payload)
          .then((res) => {
            this.sankeyData = res.data;
          });
      } else if (this.selectedVis === "chord") {
        axios.post("http://localhost:8080/createChord", payload).then((res) => {
          this.chordData = res.data;
        });
      }
    },
  },
};
</script>

<style scoped>
</style>>
