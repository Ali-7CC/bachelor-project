<template>
  <div>
    <h1>Process Data Visualization</h1>
    <div id="upload-container">
      <input type="file" name="file" id="file-upload" />
      <button v-on:click="upload">Upload</button>
    </div>
    <div id="draw-container">
      <select name="file-name" id="file-name"></select>
      <select name="attribute" id="attribute"></select>
      <select name="operator" id="operator"></select>
      <select name="aggregator" id="aggregator"></select>
      <button>Draw</button>
    </div>
    <div id="tabs">
      <b-tabs content-class="mt-3">
        <b-tab title="Sankey" active>
            
        </b-tab>
        <b-tab title="Chord"></b-tab>

      </b-tabs>
    </div>
  </div>
</template>

<script>
export default {
    data(){
        return{
            fileNames : [],
            fileAttributes : {}
        }
    },

    methods : {
        upload : function() {
            
        }
    }

};
</script>

<style scoped>
</style>>
