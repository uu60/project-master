<template>
  <div id="app">
    <router-view></router-view>

  </div>
</template>

<script>
import MainComponent from "@/components/MainComponent";
import SearchPage from "@/pages/SearchPage";
import LoginPage from "@/pages/LoginPage";

export default {
  name: 'App',
  components: {
    LoginPage,
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
