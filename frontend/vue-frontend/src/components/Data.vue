<template>
  <div>
    <h1>Process Data Visualization</h1>
    <div id="upload-container">
      <input type="file" accept=".xes" v-on:change="onFileSelected" />
      <button v-on:click="onUpload">Upload</button>
    </div>
    <div id="draw-container">
      <select v-model="selectedFileDraw">
        <option value=null>Select a file</option>
        <option v-for="file in this.files" v-bind:key="file.name">
          {{ file.name }}
        </option>
      </select>
      <select v-model="selectedAttr">
        <option value="">Select an attribute</option>
        <template v-if="selectedFileDraw !=null">
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
        <option value="MAX">Max</option>
        <option value="MIN">Min</option>
        <option value="AVG">Average</option>
      </select>
      <button v-on:click="onDraw">Draw</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      // Children
      visualizations : [],
      // File upload
      selectedFileUpload: "",
      files: [],
      // Drawing
      selectedFileDraw: null,
      selectedAttr: "",
      selectedOp: "",
      selectedAgg: "",
    };
  },

  created(){
    this.visualizations = this.$children;
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
        console.log(newFile);
        this.selectedFileUpload = null;
      });
    },

    onDraw : () => {
      const payload = new FormData();
      payload.append("fileName", this.selectedFileDraw);
      payload.append("attributeKey", this.selectedAttr);
      payload.append("operation", this.selectedOp);
      payload.append("aggregationFunc", this.selectedAgg);

      // axios.post("http://localhost:8080/sankey",payload.then(res => {


      // }))

    }
  },
};
</script>

<style scoped>
</style>>
