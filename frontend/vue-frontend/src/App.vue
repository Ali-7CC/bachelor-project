<template>
  <div id="app">
    <Data> </Data>
    <Sankey></Sankey> 
  </div>
</template>

<script>
import Data from "./components/Data.vue";
import Sankey from "./components/Sankey.vue";

export default {
  name: "App",
  components: {
    Data,
    Sankey,
  },
};
</script>

<style>
</style>
