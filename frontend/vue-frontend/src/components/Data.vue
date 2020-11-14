<template>
  <div>
    <h1>Process Data Visualization</h1>
    <div id="upload-container">
      <input type="file" accept=".xes" v-on:change="onFileSelected" />
      <button v-on:click="onUpload">Upload</button>
    </div>
    <div id="draw-container">
      <select>
        <!-- v-for option from file -->
      </select>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      selectedFileUpload: null,
      files : {}
    };
  },

  methods: {
    onFileSelected: function (event) {
      this.selectedFileUpload = event.target.files[0];
    },

    onUpload: function () {
      const payload = new FormData();
      payload.append("file", this.selectedFileUpload);
      axios
        .post("http://localhost:8080/upload", payload)
        .then((res) => {
            this.files[this.selectedFileUpload.name] = res.data;
            this.selectedFileUpload = null;
        });
    },
  },
};
</script>

<style scoped>
</style>>
