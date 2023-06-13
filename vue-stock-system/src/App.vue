<template>
  <div id="app">
    <MainComponent/>
  </div>
</template>

<script>
import MainComponent from "@/components/MainComponent";


export default {
  name: 'App',
  components: {
    MainComponent
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
