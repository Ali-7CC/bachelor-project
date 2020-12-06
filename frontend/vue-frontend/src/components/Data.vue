<template>
  <div>
    <h1>Process Data Visualization</h1>
    <div id="upload-container">
      <b-form-file
        class="w-75"
        size="sm"
        type="file"
        accept=".xes"
        v-on:change="onFileSelected"
      />
      <b-button v-on:click="onUpload">Upload</b-button>
    </div>
    <div id="draw-container">
      <b-dropdown v-on:change="onFileChange" v-model="selectedFileNameToDraw">
        <b-dropdown-item v-for="file in this.files" v-bind:key="file.name">
          <b-dropdown-item value="">Select a file</b-dropdown-item>
          {{ file.name }}
        </b-dropdown-item>
      </b-dropdown>
      <select v-model="selectedAttr">
        <option value="">Select an attribute</option>
        <template v-if="selectedFileToDraw != null">
          <option
            v-for="attr in selectedFileToDraw.attributes"
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

    <div id="slider-container">
      <span id="minus-button">
        <button @click="onSliderDec">-</button>
      </span>
      <input
        type="range"
        id="slider"
        :min="min"
        :max="max"
        v-model="sliderPosition"
        @change="onDraw()"
      />
      <span id="plus-button">
        <button @click="onSliderInc">+</button>
      </span>

      <span id="slider-value">
        Percentage: {{ parseFloat((selectedPercentage * 100).toFixed(2)) }}%
      </span>
      <span id="slider-disc"> Variants: {{ variantNums }}</span>
    </div>
    <div id="tabs">
      <button v-for="tab in tabs" :key="tab" :value="tab" @click="onTabChange">
        {{ tab }}
      </button>
      <keep-alive>
        <component
          :is="currentTabComponent"
          :raw-data="currentProps"
        ></component>
      </keep-alive>
    </div>
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
      selectedFileNameToDraw: "",
      // selectedPercentage: "",
      selectedAttr: "",
      selectedOp: "",
      selectedAgg: "",
      sliderPosition: 0,
      // Tabs
      tabs: ["Sankey", "Chord"],
      currentTab: "Sankey",
      // Sankey data
      sankeyData: "",
      // Chord Data
      chordData: "",
      // Last parameters
      lastParameters: {
        Sankey: {
          selectedFileNameToDraw: "",
          selectedAttr: "",
          selectedOp: "",
          selectedAgg: "",
          sliderPosition: 0,
        },
        Chord: {
          selectedFileNameToDraw: "",
          selectedAttr: "",
          selectedOp: "",
          selectedAgg: "",
          sliderPosition: 0,
        },
      },
    };
  },

  computed: {
    noEmptyParam: function () {
      return (
        this.selectedFileNameToDraw != "" &&
        this.selectedAttr != "" &&
        this.selectedOp != "" &&
        this.selectedAgg != ""
      );
    },
    selectedFileToDraw: function () {
      return this.selectedFileNameToDraw === ""
        ? null
        : this.files.find((f) => f.name === this.selectedFileNameToDraw);
    },
    variantNums: function () {
      return this.selectedFileToDraw === null
        ? 0
        : parseInt(this.sliderPosition) + 1;
    },
    min: function () {
      return 0;
    },

    max: function () {
      return this.selectedFileToDraw === null
        ? 0
        : this.selectedFileToDraw.percentages.length - 1;
    },

    selectedPercentage: function () {
      return this.selectedFileToDraw === null
        ? 0
        : this.selectedFileToDraw.percentages[this.sliderPosition];
    },

    currentTabComponent: function () {
      return this.currentTab.toLowerCase();
    },
    currentProps: function () {
      if (this.currentTabComponent === "sankey") {
        return this.sankeyData;
      } else {
        return this.chordData;
      }
    },
  },
  // watch: {
  //   sliderPosition: function () {
  //     this.onDraw();
  //   },
  // },
  methods: {
    onTabChange: function (e) {
      const tab = e.target.value;
      this.currentTab = tab;
      this.selectedFileNameToDraw = this.lastParameters[
        tab
      ].selectedFileNameToDraw;
      this.selectedAttr = this.lastParameters[tab].selectedAttr;
      this.selectedOp = this.lastParameters[tab].selectedOp;
      this.selectedAgg = this.lastParameters[tab].selectedAgg;
      this.sliderPosition = this.lastParameters[tab].sliderPosition;
    },
    onFileChange: function () {
      this.sliderPosition = 0;
    },
    onSliderInc: function () {
      if (this.sliderPosition < this.max && this.selectedFileToDraw != null) {
        this.sliderPosition++;
        this.onDraw();
      }
    },

    onSliderDec: function () {
      if (this.sliderPosition > this.min && this.selectedFileToDraw != null) {
        this.sliderPosition--;
        this.onDraw();
      }
    },
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
          percentages: res.data.percentages,
        };
        this.files.push(newFile);
        this.selectedFileUpload = null;
      });
    },

    onDraw: function () {
      if (this.noEmptyParam) {
        // Payload
        const payload = new FormData();
        payload.append(
          "fileName",
          this.selectedFileNameToDraw.split(".xes")[0] +
            "_" +
            this.selectedPercentage +
            ".xes"
        );
        payload.append("attributeKey", this.selectedAttr);
        payload.append("operation", this.selectedOp);
        payload.append("aggregationFunc", this.selectedAgg);
        if (this.currentTabComponent === "sankey") {
          this.lastParameters.Sankey.selectedFileNameToDraw = this.selectedFileNameToDraw;
          this.lastParameters.Sankey.selectedAttr = this.selectedAttr;
          this.lastParameters.Sankey.selectedOp = this.selectedOp;
          this.lastParameters.Sankey.selectedAgg = this.selectedAgg;
          this.lastParameters.Sankey.sliderPosition = this.sliderPosition;
          axios
            .post("http://localhost:8080/createSankey", payload)
            .then((res) => {
              this.sankeyData = res.data;
            });
        } else if (this.currentTabComponent === "chord") {
          this.lastParameters.Chord.selectedFileNameToDraw = this.selectedFileNameToDraw;
          this.lastParameters.Chord.selectedAttr = this.selectedAttr;
          this.lastParameters.Chord.selectedOp = this.selectedOp;
          this.lastParameters.Chord.selectedAgg = this.selectedAgg;
          this.lastParameters.Chord.sliderPosition = this.sliderPosition;
          axios
            .post("http://localhost:8080/createChord", payload)
            .then((res) => {
              this.chordData = res.data;
            });
        }
      } else {
        alert("Not all parameters have been selected");
      }
    },
  },
};
</script>

<style scoped>
</style>>
