<template>
  <div>
    <el-container>
      <el-header>
        <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" @select="handleSelect">
          <el-menu-item index="/Home">首页</el-menu-item>
          <el-menu-item index="/Menu2">菜单二</el-menu-item>
        </el-menu>

        
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>


<script>


export default {
  name: "MainComponent",
  components: {

  },
  data() {
    return {
      activeIndex: '/Home',
    };
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
      this.$router.push(key);
    }
  }
}
</script>

<style scoped lang="less">
.col1 {
  background-color: rgb(237, 250, 177);
  color: aliceblue;
}

.col2 {
  background-color: rgb(230, 164, 250);
  color: aliceblue;
}

.col3 {
  background-color: rgb(141, 123, 241);
  color: aliceblue;
}

//header {
//  height: 1rem;
//  width: 100%;
//  background-color: rgba(0, 0, 255, .2);
//
//  h1 {
//    font-size: 0.375rem;
//    color: white;
//    text-align: center;
//    line-height: 1rem;
//  }
//}


</style>