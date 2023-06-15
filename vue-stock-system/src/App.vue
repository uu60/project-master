<template>
  <div id="app">
    <MainComponent/>
  </div>
</template>

<script>
import MainComponent from "@/components/MainComponent";
import SearchPage from "@/pages/SearchPage";

export default {
  name: 'App',
  components: {
    MainComponent,
    SearchPage
  }
}
</script>

<style lang="less">
  //body{
  //  background: url("~@/assets/bg.jpg") top center no-repeat;
  //}
  *{
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
</style>
